/**
 * Secure Firebase Cloud Function proxy for Google Cloud Text-to-Speech (Chirp 3 HD).
 * Keeps all Google Cloud credentials strictly server-side — ZERO API keys in Android APK.
 *
 * Deployment:
 *   cd backend/cloud-function-tts && npm install
 *   firebase deploy --only functions:synthesizeChirp3Hd
 */
const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const textToSpeech = require("@google-cloud/text-to-speech");
const crypto = require("crypto");

admin.initializeApp();
const ttsClient = new textToSpeech.TextToSpeechClient();
const memoryCache = new Map();

const ALLOWED_VOICES = new Set([
  "en-GB-Chirp3-HD-Aoede",
  "en-US-Chirp3-HD-Aoede",
  "en-GB-Chirp3-HD-Kore",
  "en-US-Chirp3-HD-Kore",
  "en-GB-Chirp3-HD-Charon",
  "en-US-Chirp3-HD-Charon",
  "en-GB-Chirp3-HD-Puck",
  "en-US-Chirp3-HD-Puck"
]);

exports.synthesizeChirp3Hd = onRequest(
  { region: "europe-west1", cors: true, maxInstances: 10 },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({ error: "Method Not Allowed" });
      return;
    }

    try {
      // Verify Firebase Auth ID token
      const authHeader = req.headers.authorization || "";
      if (!authHeader.startsWith("Bearer ")) {
        res.status(401).json({ error: "Missing Firebase Auth Bearer token" });
        return;
      }
      const idToken = authHeader.slice("Bearer ".length).trim();
      await admin.auth().verifyIdToken(idToken);

      const {
        text,
        languageCode = "en-GB",
        voiceName = "en-GB-Chirp3-HD-Aoede",
        speakingRate = 0.96
      } = req.body || {};

      if (typeof text !== "string" || !text.trim() || text.length > 2500) {
        res.status(400).json({ error: "Invalid text payload (1..2500 chars required)." });
        return;
      }

      const safeLang = languageCode === "en-US" ? "en-US" : "en-GB";
      const safeVoice = ALLOWED_VOICES.has(voiceName)
        ? voiceName
        : `${safeLang}-Chirp3-HD-Aoede`;
      const safeRate = Math.min(1.4, Math.max(0.65, Number(speakingRate) || 0.96));

      const cacheKey = crypto
        .createHash("sha256")
        .update(`${safeLang}|${safeVoice}|${safeRate.toFixed(2)}|${text.trim()}`)
        .digest("hex");

      if (memoryCache.has(cacheKey)) {
        const cachedBuf = memoryCache.get(cacheKey);
        res.set("Content-Type", "audio/mpeg");
        res.set("X-Audio-Cache", "HIT");
        res.status(200).send(cachedBuf);
        return;
      }

      const [response] = await ttsClient.synthesizeSpeech({
        input: { text: text.trim() },
        voice: {
          languageCode: safeLang,
          name: safeVoice
        },
        audioConfig: {
          audioEncoding: "MP3",
          speakingRate: safeRate
        }
      });

      const audioBuffer = Buffer.isBuffer(response.audioContent)
        ? response.audioContent
        : Buffer.from(response.audioContent, "base64");

      if (memoryCache.size > 200) {
        const firstKey = memoryCache.keys().next().value;
        memoryCache.delete(firstKey);
      }
      memoryCache.set(cacheKey, audioBuffer);

      res.set("Content-Type", "audio/mpeg");
      res.set("X-Audio-Cache", "MISS");
      res.status(200).send(audioBuffer);
    } catch (err) {
      console.error("synthesizeChirp3Hd failed:", err);
      const status = err?.code === "auth/argument-error" ? 401 : 500;
      res.status(status).json({ error: "Speech synthesis request failed." });
    }
  }
);
