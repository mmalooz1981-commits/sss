package com.example.data

object CurriculumUnits1to4 {

    val unit1 = BookUnit(
        number = 1,
        titleEn = "Where are you from?",
        titleAr = "من أين أنت؟ — البلدان والجنسيات",
        icon = "🌍",
        startPage = 6,
        lessons = listOf(
            BookLesson(
                unitNumber = 1,
                lessonNumber = 1,
                titleEn = "I'm from ...",
                titleAr = "أنا من...",
                pageNumber = 6,
                summaryAr = "التعرف على أسماء البلدان الثمانية واستخدام السؤال عن البلد مع Is و Are.",
                keySentences = listOf(
                    "My name's Dima. I'm from Iraq." to "اسمي ديما. أنا من العراق.",
                    "Firas isn't from Japan. He's from Iraq." to "فراس ليس من اليابان. هو من العراق.",
                    "We aren't from Italy. We're from Turkey." to "نحن لسنا من إيطاليا. نحن من تركيا.",
                    "Are Murad and Ali from Turkey? Yes, they are." to "هل مراد وعلي من تركيا؟ نعم، هم كذلك."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Is / Are ... from?",
                    titleAr = "قاعدة السؤال بـ Is و Are عن البلد",
                    explanationAr = "نستخدم Is مع المفرد (he / she)، ونستخدم Are مع الجمع والمخاطب (we / you / they).",
                    examples = listOf(
                        "Is he/she from Iraq? -> Yes, he/she is. / No, he/she isn't." to "للمفرد المذكر والمؤنث",
                        "Are they from Turkey? -> Yes, they are. / No, they aren't." to "للجمع"
                    )
                )
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 2,
                titleEn = "This is my flag.",
                titleAr = "هذا علمي",
                pageNumber = 8,
                summaryAr = "التعرف على أعلام البلدان والتحدث عن الأطفال وبلدانهم (Peter, Enzo, Hiroshi, Minoru, Dalia, Kanda, Chloe, Amelie).",
                keySentences = listOf(
                    "Peter isn't from France. He's from England." to "بيتر ليس من فرنسا. هو من إنجلترا.",
                    "Chloe and Amelie are from France." to "كلوي وأميلي من فرنسا.",
                    "This is my flag. I'm from Iraq. I love my country." to "هذا علمي. أنا من العراق. أنا أحب بلدي.",
                    "Where is Kanda from? She's from Thailand." to "من أين كاندا؟ هي من تايلاند."
                )
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 3,
                titleEn = "My nationality",
                titleAr = "جنسيتي",
                pageNumber = 10,
                summaryAr = "التحويل من اسم البلد إلى الجنسية (Iraq -> Iraqi, Japan -> Japanese, Italy -> Italian, Thailand -> Thai, France -> French, Egypt -> Egyptian, Turkey -> Turkish, England -> English).",
                keySentences = listOf(
                    "What nationality are you? I'm Iraqi." to "ما هي جنسيتك؟ أنا عراقي.",
                    "What nationality is he? He is Japanese." to "ما هي جنسيته؟ هو ياباني.",
                    "We're Turkish." to "نحن أتراك.",
                    "Are Chloe and Amelie French? Yes, they are French." to "هل كلوي وأميلي فرنسيتان؟ نعم، هما فرنسيتان."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Countries & Nationalities",
                    titleAr = "البلدان والجنسيات",
                    explanationAr = "تُكتب أسماء البلدان والجنسيات دائماً بحرف كبير (Capital letter) في بدايتها.",
                    examples = listOf(
                        "Iraq -> Iraqi | Egypt -> Egyptian" to "العراق -> عراقي | مصر -> مصري",
                        "Japan -> Japanese | France -> French" to "اليابان -> ياباني | فرنسا -> فرنسي",
                        "Turkey -> Turkish | England -> English" to "تركيا -> تركي | إنجلترا -> إنجليزي",
                        "Italy -> Italian | Thailand -> Thai" to "إيطاليا -> إيطالي | تايلاند -> تايلاندي"
                    )
                )
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 4,
                titleEn = "Where's it from?",
                titleAr = "من أين هذا؟",
                pageNumber = 12,
                summaryAr = "التعرف على الحيوانات والنباتات والأطعمة الوطنية والقارات في بلدان العالم (تركيا، تايلاند، اليابان، إنجلترا، أستراليا، الصين، الهند، إيطاليا).",
                keySentences = listOf(
                    "Pandas are from China." to "الباندا من الصين.",
                    "The kangaroo is the national animal of Australia." to "الكنغر هو الحيوان الوطني لأستراليا.",
                    "Tigers are the national animal of India." to "النمور هي الحيوان الوطني للهند.",
                    "The national sport of China is table tennis." to "الرياضة الوطنية للصين هي تنس الطاولة."
                )
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 5,
                titleEn = "Learning English",
                titleAr = "تعلم اللغة الإنجليزية",
                pageNumber = 14,
                summaryAr = "أسباب تعلم اللغة الإنجليزية باستخدام (I want to learn English because ...).",
                keySentences = listOf(
                    "I want to study in the English language." to "أريد أن أدرس باللغة الإنجليزية.",
                    "Lots of people in the world speak English." to "الكثير من الناس في العالم يتحدثون الإنجليزية.",
                    "I want to travel to other countries." to "أريد أن أسافر إلى بلدان أخرى.",
                    "There is a lot of English on the internet." to "يوجد الكثير من الإنجليزية على الإنترنت."
                )
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 6,
                titleEn = "My Magazine: Country Information File",
                titleAr = "مجلتي: ملف معلومات البلدان",
                pageNumber = 16,
                summaryAr = "قراءة تقارير جيك (من لندن في إنجلترا) وتانفي (من مومباي في الهند) والتعرف على المعالم المشهورة.",
                keySentences = listOf(
                    "London is the capital of England." to "لندن هي عاصمة إنجلترا.",
                    "The London Eye is a very big wheel." to "عين لندن هي عجلة كبيرة جداً.",
                    "The capital of India is New Delhi, but Mumbai is the biggest city." to "عاصمة الهند نيودلهي، لكن مومباي أكبر مدينة.",
                    "The Taj Mahal is a beautiful monument in India." to "تاج محل معلم جميل في الهند."
                ),
                readingTitle = "Jake (England) & Tanvi (India)",
                readingTextEn = "Hello! My name is Jake, and I'm English. England is in Europe. I live in London. London is the capital of England. My favourite is the London Eye. It is a very big wheel. Tower Bridge is very old. My name is Tanvi. I live in Mumbai. It is the biggest city, but the capital of India is New Delhi. Riya is my best friend. This photo is the beautiful Taj Mahal."
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 7,
                titleEn = "Check my understanding: Lebanon & Iraq",
                titleAr = "ملف معلومات لبنان والعراق",
                pageNumber = 18,
                summaryAr = "التعرف على معلومات لبنان (العاصمة بيروت، شجرة الأرز، 225 كم ساحل) والعراق (بغداد، دجلة والفرات، بابل، الأهوار، الملوية، جبال زاكروس، شلال بيخال).",
                keySentences = listOf(
                    "The capital of Lebanon is Beirut." to "عاصمة لبنان هي بيروت.",
                    "The cedar tree is the symbol of Lebanon." to "شجرة الأرز هي رمز لبنان.",
                    "Iraq is a country in Southwest Asia. The capital is Baghdad." to "العراق بلد في جنوب غرب آسيا، والعاصمة بغداد.",
                    "There are two very famous rivers, the Tigris and Euphrates." to "يوجد نهران مشهوران جداً هما دجلة والفرات."
                ),
                readingTitle = "About Iraq (Page 19)",
                readingTextEn = "Iraq is a country in Southwest Asia. The capital is Baghdad. There are high mountains in the north, and there are two very famous rivers, the Tigris and Euphrates. In Iraq, people speak Arabic and Kurdish. The Iraqi flag is red, white and black with green Arabic writing Allahu Akbar in the middle. Interesting places include Babylon, the Marshes, Malwiya Minaret, the Zagros mountains and Bekhal waterfall."
            ),
            BookLesson(
                unitNumber = 1,
                lessonNumber = 8,
                titleEn = "Let's sing! Let's be friends! & Oman Project",
                titleAr = "أنشودة هيا نصبح أصدقاء ومشروع عُمان",
                pageNumber = 20,
                summaryAr = "أنشودة البلدان والجنسيات وملصق سلطنة عُمان (مسقط، المها العربي، الحلوى العُمانية).",
                keySentences = listOf(
                    "We all live together, so let's hold hands! Let's be friends!" to "نحن جميعاً نعيش معاً، فلنمسك الأيدي ونكن أصدقاء!",
                    "The capital of Oman is Muscat." to "عاصمة عُمان هي مسقط.",
                    "The national animal of Oman is the Arabian oryx." to "الحيوان الوطني لعُمان هو المها العربي."
                ),
                readingTitle = "Rhyme: Let's be friends!",
                readingTextEn = "Iraq, Turkey, Egypt, Japan: One country, two countries, three countries, four. You know all the names, so let's say some more! Thailand, Canada, India, Italy. I'm Iraqi. What's your nationality? I'm English. I'm Russian. I'm French. And he's from Thailand. We all live together, so let's hold hands! We all live together, so let's be friends!"
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u1_fb1", 1, "___ Dalia from Egypt?", "اسأل عن داليا (مفرد مؤنث)", listOf("Is", "Are", "Am", "Do"), "Is", "نستخدم Is مع المفرد (she / Dalia)."),
            FillBlankItem("u1_fb2", 1, "___ Murad and Ali from Turkey?", "اسأل عن مراد وعلي (جمع)", listOf("Is", "Are", "Am", "Does"), "Are", "نستخدم Are مع الجمع (Murad and Ali / they)."),
            FillBlankItem("u1_fb3", 1, "Firas isn't from Japan. ___ from Iraq.", "اختر الضمير المناسب لفراس", listOf("He's", "She's", "They're", "It's"), "He's", "فراس مذكر مفرد، لذا نستخدم He's."),
            FillBlankItem("u1_fb4", 1, "What nationality are you? I'm ___.", "الجنسية من Iraq", listOf("Iraq", "Iraqi", "Iraqis", "English"), "Iraqi", "جنسية العراق هي Iraqi."),
            FillBlankItem("u1_fb5", 1, "Enzo is from Italy. He is ___.", "الجنسية من Italy", listOf("Italy", "Italian", "French", "Turkish"), "Italian", "جنسية إيطاليا هي Italian."),
            FillBlankItem("u1_fb6", 1, "The capital of Lebanon is ___.", "عاصمة لبنان من الكتاب صفحة 18", listOf("Beirut", "Baghdad", "London", "Muscat"), "Beirut", "عاصمة لبنان هي Beirut."),
            FillBlankItem("u1_fb7", 1, "Are Hiroshi and Minoru from Thailand? No, they ___.", "نفي الإجابة للجمع", listOf("aren't", "isn't", "don't", "are"), "aren't", "في النفي مع they نستخدم aren't.")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u1_so1", 1, "Where are you from?", "من أين أنت؟"),
            SentenceOrderItem("u1_so2", 1, "I'm from Iraq.", "أنا من العراق."),
            SentenceOrderItem("u1_so3", 1, "What nationality is he?", "ما هي جنسيته؟"),
            SentenceOrderItem("u1_so4", 1, "Pandas are from China.", "الباندا من الصين."),
            SentenceOrderItem("u1_so5", 1, "London is the capital of England.", "لندن هي عاصمة إنجلترا."),
            SentenceOrderItem("u1_so6", 1, "I want to learn English.", "أريد أن أتعلم اللغة الإنجليزية.")
        ),
        dialogues = listOf(
            DialogueExercise("u1_d1", 1, "Jena", "Is Dima from England?", "هل ديما من إنجلترا؟", listOf("No, she isn't. She's from Iraq.", "Yes, he is.", "No, they aren't.", "I'm Thai."), "No, she isn't. She's from Iraq.", "ديما من العراق، فنقول No, she isn't. She's from Iraq."),
            DialogueExercise("u1_d2", 1, "Jena", "What nationality are you?", "ما هي جنسيتك؟", listOf("I'm Iraqi.", "I'm from Baghdad.", "Yes, they are.", "It's in Asia."), "I'm Iraqi.", "للسؤال عن الجنسية نجيب بـ I'm + الجنسية."),
            DialogueExercise("u1_d3", 1, "Karam", "Where is Kanda from?", "من أين كاندا؟", listOf("She's from Thailand.", "He's from France.", "They're from Japan.", "Yes, she is."), "She's from Thailand.", "كاندا فتاة من تايلاند (صفحة 9).")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u1_rq1", 1, "Country Information File (p.18)", "Is Jake French?", "هل جيك فرنسي؟", listOf("Yes", "No"), "No", "جيك إنجليزي ويعيش في لندن."),
            ReadingQuestion("u1_rq2", 1, "Country Information File (p.18)", "The London Eye is a big wheel.", "عين لندن هي عجلة كبيرة.", listOf("Yes", "No"), "Yes", "قال جيك: My favourite is the London Eye. It is a very big wheel."),
            ReadingQuestion("u1_rq3", 1, "Country Information File (p.18)", "What is the capital of India?", "ما هي عاصمة الهند؟", listOf("New Delhi", "Mumbai", "London", "Beirut"), "New Delhi", "مومباي أكبر مدينة، لكن العاصمة هي New Delhi."),
            ReadingQuestion("u1_rq4", 1, "About Iraq (p.19)", "What are the two famous rivers in Iraq?", "ما هما النهران المشهوران في العراق؟", listOf("Tigris and Euphrates", "Nile and Jordan", "Thames and Seine", "Indus and Ganges"), "Tigris and Euphrates", "دجلة والفرات: Tigris and Euphrates."),
            ReadingQuestion("u1_rq5", 1, "Where's it from? (p.13)", "What is the national animal of Australia?", "ما هو الحيوان الوطني لأستراليا؟", listOf("Kangaroo", "Panda", "Tiger", "Wolf"), "Kangaroo", "الكنغر Kangaroo هو الحيوان الوطني لأستراليا.")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 2,
                unitNumber = 1,
                titleEn = "Track 2: Look, listen and say (Countries)",
                titleAr = "المقطع 2: أسماء البلدان مع ديما",
                scriptEn = "Dima: Hello, my name's Dima. I'm from Iraq. Let's say the country names. 1 England, 2 France, 3 Italy, 4 Egypt, 5 Turkey, 6 Iraq, 7 Japan, 8 Thailand.",
                summaryAr = "تقدم ديما نفسها من العراق وتقرأ أسماء البلدان الثمانية على الخريطة.",
                questionEn = "Where is Dima from?",
                options = listOf("Iraq", "England", "Japan", "France"),
                correctAnswer = "Iraq"
            ),
            ListeningTrack(
                trackNumber = 4,
                unitNumber = 1,
                titleEn = "Track 4: Children and their flags",
                titleAr = "المقطع 4: الأطفال وأعلام بلدانهم",
                scriptEn = "1 Hello, I'm Peter. I'm from England. 2 My name's Enzo. I'm from Italy. My flag is green, white and red. 3 Hello, we're Hiroshi and Minoru. We're from Japan. 4 I'm Dalia, and I'm from Egypt. 5 Hi, I'm from Thailand. My name is Kanda. 6 We are Chloe and Amelie, and we're from France.",
                summaryAr = "يعرف ستة أطفال عن أنفسهم وعن بلدانهم وأعلامهم.",
                questionEn = "Where are Hiroshi and Minoru from?",
                options = listOf("Japan", "Italy", "Egypt", "France"),
                correctAnswer = "Japan"
            ),
            ListeningTrack(
                trackNumber = 10,
                unitNumber = 1,
                titleEn = "Track 10: Why do you want to learn English?",
                titleAr = "المقطع 10: لماذا نريد تعلم الإنجليزية؟",
                scriptEn = "Jena: I want to learn English because I want to study in the English language. Karam: Because lots of people in the world speak English. Dima: Because lots of books, films and songs are in English. Firas: Because I want to travel to other countries. Zina: Because the internet uses English a lot.",
                summaryAr = "يشرح التلاميذ أسباب حبهم لتعلم اللغة الإنجليزية.",
                questionEn = "Why does Firas want to learn English?",
                options = listOf("To travel to other countries", "To sing songs", "To use the internet", "To read books"),
                correctAnswer = "To travel to other countries"
            ),
            ListeningTrack(
                trackNumber = 13,
                unitNumber = 1,
                titleEn = "Track 13: Listen and read about Iraq",
                titleAr = "المقطع 13: استمع واقرأ عن العراق",
                scriptEn = "Iraq is a country in Southwest Asia. The capital is Baghdad. There are high mountains in the north, and there are two very famous rivers, the Tigris and Euphrates. In Iraq, people speak Arabic and Kurdish. There are lots of interesting places to see, like Babylon, the Marshes, Malwiya Minaret, the Zagros mountains and Bekhal waterfall.",
                summaryAr = "نص معلوماتي عن العراق وعاصمته بغداد ولغاته وأهم معالمه السياحية.",
                questionEn = "What languages do people speak in Iraq?",
                options = listOf("Arabic and Kurdish", "French and English", "Turkish and Arabic", "Japanese and English"),
                correctAnswer = "Arabic and Kurdish"
            )
        )
    )

    val unit2 = BookUnit(
        number = 2,
        titleEn = "Routines and animals",
        titleAr = "الروتين اليومي والحيوانات والحشرات",
        icon = "🐘",
        startPage = 22,
        lessons = listOf(
            BookLesson(
                unitNumber = 2,
                lessonNumber = 1,
                titleEn = "About time",
                titleAr = "عن الوقت",
                pageNumber = 22,
                summaryAr = "قراءة الساعة والسؤال عن أوقات الروتين اليومي (get up, go to school, finish school, have lunch, have supper, go to bed).",
                keySentences = listOf(
                    "What time is it? It's seven forty-five." to "كم الساعة؟ إنها السابعة وخمس وأربعون دقيقة.",
                    "What time do you get up? I get up at 7 o'clock." to "متى تستيقظ؟ أستيقظ الساعة السابعة.",
                    "I finish school at 12:30." to "أنهي المدرسة الساعة 12:30.",
                    "I go to bed at 9 o'clock." to "أذهب إلى النوم الساعة التاسعة."
                )
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 2,
                titleEn = "Mr Hadi's day",
                titleAr = "يوم السيد هادي",
                pageNumber = 24,
                summaryAr = "يوميات السيد هادي حارس حديقة الحيوانات مع قاعدة إضافة (s) للفعل في المضارع البسيط مع He/She/It.",
                keySentences = listOf(
                    "Mr Hadi gets up at six thirty." to "يستيقظ السيد هادي الساعة 6:30.",
                    "He leaves the house at seven thirty." to "يغادر المنزل الساعة 7:30.",
                    "First, he opens the zoo at eight o'clock." to "أولاً، يفتح حديقة الحيوانات الساعة 8:00.",
                    "After that, he feeds the animals at eleven fifteen." to "بعد ذلك، يطعم الحيوانات الساعة 11:15."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Present Simple (get / gets)",
                    titleAr = "المضارع البسيط مع الضمائر",
                    explanationAr = "مع (I, You, We, They) يبقى الفعل مجرداً (get up, have). ومع (He, She, It) نضيف (s) أو نستخدم (has).",
                    examples = listOf(
                        "I / You / We / They get up at 7:00." to "فعل مجرد بدون s",
                        "He / She / It gets up at 7:00. | She has breakfast." to "مع إضافة s أو has"
                    )
                )
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 3,
                titleEn = "Things we do every day",
                titleAr = "أشياء نفعلها كل يوم وأيام الأسبوع",
                pageNumber = 26,
                summaryAr = "الأنشطة المنزلية وأيام الأسبوع السبعة (Monday to Sunday) والسؤال باستخدام Does she ...? / When does she ...?.",
                keySentences = listOf(
                    "Does she clean the house on Thursday? Yes, she does." to "هل تنظف المنزل يوم الخميس؟ نعم، تفعل.",
                    "Does she cook on Sunday? No, she doesn't." to "هل تطبخ يوم الأحد؟ لا، لا تفعل.",
                    "When does she wash clothes? On Wednesday." to "متى تغسل الملابس؟ يوم الأربعاء.",
                    "What does she do on Tuesday? She sees her friends." to "ماذا تفعل يوم الثلاثاء؟ ترى صديقاتها."
                )
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 4,
                titleEn = "What's it like?",
                titleAr = "كيف تبدو؟ صفات الحيوانات",
                pageNumber = 28,
                summaryAr = "الصفات الإيجابية (intelligent, friendly, gentle, playful) والصفات السلبية (lazy, dangerous, greedy) واستخدام It للحيوانات.",
                keySentences = listOf(
                    "A horse is fast." to "الحصان سريع.",
                    "A shark is dangerous." to "سمك القرش خطير.",
                    "A cat is playful." to "القطة مرحة وكثيرة اللعب.",
                    "It's friendly and intelligent. It's a dolphin!" to "إنه ودود وذكي. إنه الدلفين!"
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! He / She / It",
                    titleAr = "الضمائر للأشخاص والحيوانات",
                    explanationAr = "نستخدم He و She للأشخاص فقط، أما It فنستخدمها للحيوانات والأشياء.",
                    examples = listOf(
                        "He and she are for people." to "He و She للأشخاص",
                        "It is for animals and for things." to "It للحيوانات والأشياء"
                    )
                )
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 5,
                titleEn = "Thinking about animals",
                titleAr = "التفكير في الحيوانات",
                pageNumber = 30,
                summaryAr = "التعبير عن الرأي بالحيوانات (I think sharks are dangerous) ووصف الأفعى والقرش والحصان.",
                keySentences = listOf(
                    "The snake is large and fat. It sleeps a lot - it's lazy." to "الأفعى كبيرة وسمينة. تنام كثيراً - إنها كسولة.",
                    "The shark isn't gentle. It has small eyes and very big teeth." to "القرش ليس لطيفاً. لديه عيون صغيرة وأسنان كبيرة جداً.",
                    "The horse is fast. It's brown with a long tail. It eats grass." to "الحصان سريع. لونه بني وذيله طويل ويأكل العشب."
                )
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 6,
                titleEn = "My Magazine: Insects in our life",
                titleAr = "مجلتي: الحشرات في حياتنا",
                pageNumber = 32,
                summaryAr = "حقائق علمية عن الفراشات (أربعة أجنحة، يرقة، رحيق)، النمل (مستعمرة، يحمل 20 ضعف وزنه)، والذباب (جناحان، ينقل الأمراض).",
                keySentences = listOf(
                    "Butterflies have four wings and eat nectar from flowers." to "للفراشات أربعة أجنحة وتأكل الرحيق من الأزهار.",
                    "Most ants live together in a big house called a colony." to "يعيش معظم النمل معاً في بيت كبير يُسمى مستعمرة.",
                    "An ant can lift 20 times its own body weight." to "تستطيع النملة رفع 20 ضعف وزن جسمها.",
                    "Flies only have two wings and they carry disease." to "للذباب جناحان فقط وهو ينقل الأمراض."
                ),
                readingTitle = "Insects in our life (Page 33)",
                readingTextEn = "Butterflies are insects. They have four wings. Before you see a butterfly, it starts its life as an egg and then changes into a caterpillar. Most butterflies eat nectar from flowers. Most ants live together in a big house called a colony. An ant can lift 20 times its own body weight. Flies only have two wings. They are dangerous because they carry disease."
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 7,
                titleEn = "Check my understanding: Dove",
                titleAr = "الحمامة رمز السلام",
                pageNumber = 34,
                summaryAr = "تصحيح الجمل عن الحشرات وقراءة نص الحمامة (Dove: symbol of peace, eats seeds and fruit, lives 15-20 years).",
                keySentences = listOf(
                    "The dove is famous as the symbol of peace." to "الحمامة مشهورة بأنها رمز السلام.",
                    "Doves eat seeds and fruit." to "تأكل الحمائم البذور والفواكه.",
                    "Spiders have eight legs." to "للعناكب ثمانية أرجل."
                ),
                readingTitle = "Dove (Page 35)",
                readingTextEn = "The dove is a medium-sized bird. It is famous as the symbol of peace. It has a small head and a round body. Doves have long beaks and short legs. Doves eat seeds and fruit. This bird can live for 15 to 20 years. Doves lay 10 to 12 eggs in a year. Young doves are called squabs."
            ),
            BookLesson(
                unitNumber = 2,
                lessonNumber = 8,
                titleEn = "Story: The competition",
                titleAr = "قصة المسابقة",
                pageNumber = 36,
                summaryAr = "قصة الملك الأسد وأصدقائه: الفلامنغو تجيد الرقص، الحمامة الغناء، النسر إصلاح الأشياء، الطائر الأصفر ذكي، البومة تصنع الأعشاش، والجميع فائز!",
                keySentences = listOf(
                    "Flamingo is good at dancing." to "طائر الفلامنغو بارع في الرقص.",
                    "Eagle is strong. He is good at fixing things." to "النسر قوي وبارع في إصلاح الأشياء.",
                    "Yellow bird is intelligent. He thinks of good ideas." to "الطائر الأصفر ذكي ويفكر بأفكار جيدة.",
                    "Everyone is good at something. Lion decides everyone is a winner!" to "كل شخص بارع في شيء ما، فقرر الأسد أن الجميع فائزون!"
                ),
                readingTitle = "The competition (Pages 36-37)",
                readingTextEn = "Lion is a good king. He says: Let's have a competition! Flamingo is good at dancing. Pigeon is good at singing. Dove is gentle and nice. Eagle is strong and good at fixing things. Yellow bird is intelligent. Owl likes to sleep and is good at making nests. Lion has a picnic and decides everyone is a winner because everyone is good at something!"
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u2_fb1", 2, "Mr Hadi ___ breakfast at seven o'clock.", "المضارع البسيط مع Mr Hadi", listOf("has", "have", "having", "is"), "has", "مع المفرد المذكر Mr Hadi نستخدم has."),
            FillBlankItem("u2_fb2", 2, "He ___ the animals at eleven fifteen.", "الفعل مع He في المضارع البسيط", listOf("feeds", "feed", "feeding", "fed"), "feeds", "نضيف s للفعل مع He فتصبح feeds."),
            FillBlankItem("u2_fb3", 2, "___ is a dolphin. It is friendly and intelligent.", "اختر الضمير المناسب للحيوان", listOf("It", "He", "She", "We"), "It", "نستخدم الضمير It للحيوانات والأشياء."),
            FillBlankItem("u2_fb4", 2, "Does she clean the house on Thursday? Yes, she ___.", "إجابة قصيرة مثبتة", listOf("does", "doesn't", "is", "do"), "does", "السؤال يبدأ بـ Does فتكون الإجابة المثبتة Yes, she does."),
            FillBlankItem("u2_fb5", 2, "A shark isn't gentle. It's very ___.", "صفة سمك القرش", listOf("dangerous", "lazy", "friendly", "playful"), "dangerous", "سمك القرش خطير: dangerous."),
            FillBlankItem("u2_fb6", 2, "Most ants live in a big house called a ___.", "اسم بيت النمل من صفحة 33", listOf("colony", "cage", "nest", "park"), "colony", "يعيش النمل في مستعمرة colony.")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u2_so1", 2, "What time do you get up?", "في أي ساعة تستيقظ؟"),
            SentenceOrderItem("u2_so2", 2, "He opens the zoo at eight o'clock.", "هو يفتح حديقة الحيوانات الساعة الثامنة."),
            SentenceOrderItem("u2_so3", 2, "A shark is very dangerous.", "سمك القرش خطير جداً."),
            SentenceOrderItem("u2_so4", 2, "Most ants live in a colony.", "يعيش معظم النمل في مستعمرة."),
            SentenceOrderItem("u2_so5", 2, "Eagle is good at fixing things.", "النسر بارع في إصلاح الأشياء.")
        ),
        dialogues = listOf(
            DialogueExercise("u2_d1", 2, "Dima", "What time do you finish school?", "متى تنهي المدرسة؟", listOf("I finish school at 12:30.", "On Wednesday.", "It's dangerous.", "Yes, she does."), "I finish school at 12:30.", "السؤال عن الوقت نجيب عنه بـ at + الساعة."),
            DialogueExercise("u2_d2", 2, "Jena", "What's a dolphin like?", "كيف يبدو الدلفين؟", listOf("It's friendly and intelligent.", "It's twelve o'clock.", "On Saturday.", "She sees her friends."), "It's friendly and intelligent.", "الدلفين ودود وذكي (صفحة 29)."),
            DialogueExercise("u2_d3", 2, "Karam", "When does she wash clothes?", "متى تغسل الملابس؟", listOf("On Wednesday.", "Yes, she does.", "At the zoo.", "No, she doesn't."), "On Wednesday.", "في جدول صفحة 27 تغسل الملابس يوم الأربعاء On Wednesday.")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u2_rq1", 2, "Insects in our life (p.34)", "How many wings do flies have?", "كم جناحاً للذباب؟", listOf("Two wings", "Four wings", "Six wings", "Eight wings"), "Two wings", "للذباب جناحان فقط (Flies only have two wings)، بينما للفراشات أربعة."),
            ReadingQuestion("u2_rq2", 2, "Insects in our life (p.33)", "What do most butterflies eat?", "ماذا تأكل معظم الفراشات؟", listOf("Nectar from flowers", "Meat", "Cheese", "Seeds"), "Nectar from flowers", "تأكل الفراشات الرحيق من الأزهار."),
            ReadingQuestion("u2_rq3", 2, "Dove (p.35)", "Which bird is the famous symbol of peace?", "أي طائر هو رمز السلام المشهور؟", listOf("Dove", "Shark", "Eagle", "Owl"), "Dove", "الحمامة Dove هي رمز السلام."),
            ReadingQuestion("u2_rq4", 2, "The competition (p.36)", "What is Eagle good at?", "بماذا يبرع النسر في قصة المسابقة؟", listOf("Fixing things", "Dancing", "Singing", "Making nests"), "Fixing things", "النسر قوي وبارع في إصلاح الأشياء: fixing things.")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 18,
                unitNumber = 2,
                titleEn = "Track 18: Mr Hadi's day",
                titleAr = "المقطع 18: يوم السيد هادي في حديقة الحيوانات",
                scriptEn = "Mr Hadi gets up at six thirty. He has breakfast at seven o'clock. He leaves the house at seven thirty. First, he opens the zoo at eight o'clock. Then, he cleans the cages at ten thirty. After that, he feeds the animals at eleven fifteen. He closes the zoo at five fifteen. Finally, he gets home at five forty-five.",
                summaryAr = "جدول أعمال السيد هادي اليومي في حديقة الحيوانات من الصباح حتى المساء.",
                questionEn = "What time does Mr Hadi feed the animals?",
                options = listOf("At 11:15", "At 8:00", "At 6:30", "At 5:15"),
                correctAnswer = "At 11:15"
            ),
            ListeningTrack(
                trackNumber = 22,
                unitNumber = 2,
                titleEn = "Track 22: Days of the week routine",
                titleAr = "المقطع 22: الأنشطة خلال أيام الأسبوع",
                scriptEn = "On Monday, she always goes shopping. On Tuesday, she sees her friends. Then on Wednesday, she washes clothes. On Thursday, she cleans the house. On Friday, she helps her children with their schoolwork. On Saturday, she always cooks lots of things. Then on Sunday, she has a rest, and she watches TV.",
                summaryAr = "استمع إلى الأعمال التي تقوم بها الأم في كل يوم من أيام الأسبوع.",
                questionEn = "What does she do on Thursday?",
                options = listOf("She cleans the house", "She cooks", "She goes shopping", "She watches TV"),
                correctAnswer = "She cleans the house"
            ),
            ListeningTrack(
                trackNumber = 33,
                unitNumber = 2,
                titleEn = "Track 33: Guess the insect",
                titleAr = "المقطع 33: خمن الحشرة (الدعسوقة، النحلة، العنكبوت)",
                scriptEn = "Voice 1: These are very small, colourful insects. Red, orange or yellow with black spots. They have four wings and six legs. It's a ladybird. Voice 2: Yellow and brown, they make a buzz buzz sound and make honey from nectar. It's a bee. Voice 3: These have eight legs, not six legs. They make webs to catch flies. It's a spider.",
                summaryAr = "ألغاز ممتعة للتعرف على الدعسوقة والنحلة والعنكبوت.",
                questionEn = "Which insect makes honey from nectar?",
                options = listOf("A bee", "A spider", "A fly", "An ant"),
                correctAnswer = "A bee"
            )
        )
    )

    val unit3 = BookUnit(
        number = 3,
        titleEn = "Sports",
        titleAr = "الرياضة والأنشطة وكم مرة تفعلها",
        icon = "⚽",
        startPage = 38,
        lessons = listOf(
            BookLesson(
                unitNumber = 3,
                lessonNumber = 1,
                titleEn = "Sports I like",
                titleAr = "الرياضات التي أحبها",
                pageNumber = 38,
                summaryAr = "التعرف على 10 رياضات (basketball, swimming, football, cycling, running, karate, tennis, gymnastics, boxing, rowing).",
                keySentences = listOf(
                    "What's your favourite sport? I like swimming and tennis." to "ما هي رياضتك المفضلة؟ أحب السباحة والتنس.",
                    "I like tennis, too, but I don't like boxing." to "أحب التنس أيضاً، لكني لا أحب الملاكمة."
                )
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 2,
                titleEn = "Play or go or do?",
                titleAr = "قاعدة الأفعال play / go / do مع الرياضات",
                pageNumber = 40,
                summaryAr = "القاعدة المهمة في استخدام play للألعاب والكرة، go للرياضات المنتهية بـ ing (ما عدا boxing)، و do للرياضات الفردية والألغاز.",
                keySentences = listOf(
                    "I play football and basketball." to "ألعب كرة القدم وكرة السلة.",
                    "I go swimming, cycling, running and rowing." to "أذهب للسباحة وركوب الدراجة والجري والتجذيف.",
                    "I do karate, gymnastics and boxing." to "أمارس الكاراتيه والجمباز والملاكمة."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Play / Go / Do",
                    titleAr = "متى نستخدم Play و Go و Do؟",
                    explanationAr = "Play لألعاب الكرة والألعاب (football, tennis, basketball). Go للأنشطة المنتهية بـ ing (swimming, cycling, rowing) باستثناء boxing. Do للرياضات الفردية والقتالية (karate, gymnastics, boxing, puzzles).",
                    examples = listOf(
                        "play: football, tennis, basketball, volleyball" to "ألعاب الكرة والفرق",
                        "go: swimming, cycling, running, rowing" to "أنشطة تنتهي بـ ing",
                        "do: karate, gymnastics, boxing, puzzles" to "رياضات فردية والملاكمة"
                    )
                )
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 3,
                titleEn = "How often do you ...?",
                titleAr = "كم مرة تفعل...؟ ظروف التكرار",
                pageNumber = 42,
                summaryAr = "ظروف التكرار: always (100%)، often (75%)، sometimes (50%)، never (0%).",
                keySentences = listOf(
                    "I always clean my teeth after breakfast." to "أنا دائماً أنظف أسناني بعد الفطور.",
                    "I often go swimming on Saturday." to "أنا غالباً أذهب للسباحة يوم السبت.",
                    "I sometimes play tennis with my friends." to "أنا أحياناً ألعب التنس مع أصدقائي.",
                    "I never watch TV before school." to "أنا أبداً لا أشاهد التلفاز قبل المدرسة."
                )
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 4,
                titleEn = "At the weekend",
                titleAr = "في عطلة نهاية الأسبوع",
                pageNumber = 44,
                summaryAr = "أنشطة العطلة (see a film, go to a museum, play in the park, help at home, play with friends, have a picnic) وتكرارها (once, twice, three times).",
                keySentences = listOf(
                    "How often do you go to a museum? Once a year." to "كم مرة تذهب للمتحف؟ مرة في السنة.",
                    "How often do you help at home? Three times a week." to "كم مرة تساعد في المنزل؟ ثلاث مرات في الأسبوع.",
                    "We have a picnic twice a year." to "نخرج في نزهة مرتين في السنة."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Once / Twice / Three times",
                    titleAr = "التعبير عن عدد المرات",
                    explanationAr = "مرة واحدة = once، مرتان = twice، ثم نستخدم الرقم + times (three times, four times, five times) متبوعاً بـ a day / a week / a month / a year.",
                    examples = listOf(
                        "Once a day / Twice a week" to "مرة في اليوم / مرتان في الأسبوع",
                        "Three times a month / Four times a year" to "ثلاث مرات في الشهر / أربع مرات في السنة"
                    )
                )
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 5,
                titleEn = "A class survey",
                titleAr = "مسح ميداني للصف",
                pageNumber = 46,
                summaryAr = "قراءة مخطط الصف A وكلمات الإحصاء (all the pupils, most pupils, only eight pupils, everybody).",
                keySentences = listOf(
                    "All the pupils watch TV." to "جميع التلاميذ يشاهدون التلفاز.",
                    "Most pupils help at home." to "معظم التلاميذ يساعدون في المنزل.",
                    "Everybody in the class eats sweets." to "الجميع في الصف يأكلون الحلويات."
                )
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 6,
                titleEn = "My Magazine: Sports are great",
                titleAr = "مجلتي: الرياضة رائعة",
                pageNumber = 48,
                summaryAr = "فوائد الرياضة الستة: keep healthy, make friends, learn rules, have fun, learn a new sport, play outside.",
                keySentences = listOf(
                    "Swimming is the best way to keep healthy." to "السباحة أفضل طريقة للحفاظ على الصحة.",
                    "Being in a team is a good way to make friends." to "اللعب ضمن فريق طريقة رائعة لتكوين صداقات.",
                    "There are lots of new rules to learn in karate." to "توجد قواعد كثيرة نتعلمها في الكاراتيه."
                ),
                readingTitle = "Sports are great (Pages 48-49)",
                readingTextEn = "Why do we do sports? To keep healthy, make friends, learn rules, have fun, learn a new sport and play outside. One boy likes being in the park and prefers sports to computer games. Another boy goes swimming three times a week to keep healthy."
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 7,
                titleEn = "Check my understanding",
                titleAr = "اختبر فهمي وجدول جينا وكرم",
                pageNumber = 50,
                summaryAr = "تصحيح الجمل عن الرياضة وقراءة جدول أنشطة جينا وكرم خلال أيام الأسبوع.",
                keySentences = listOf(
                    "Do you like sports? Yes, I do." to "هل تحب الرياضة؟ نعم، أحبها.",
                    "Jena never plays football. She watches TV every day." to "جينا لا تلعب كرة القدم أبداً وتشاهد التلفاز كل يوم."
                )
            ),
            BookLesson(
                unitNumber = 3,
                lessonNumber = 8,
                titleEn = "Let's play! & Volleyball Project",
                titleAr = "هيا نلعب ومشروع كرة الطائرة",
                pageNumber = 52,
                summaryAr = "تحديات الوحدة الثالثة وملصق كرة الطائرة (6 لاعبين، شبكة في الوسط، 3 قواعد).",
                keySentences = listOf(
                    "A volleyball team has six players." to "يتكون فريق كرة الطائرة من ستة لاعبين.",
                    "Rule 1: hit the ball over the net." to "القاعدة 1: اضرب الكرة فوق الشبكة.",
                    "Rule 3: don't let the ball touch the ground." to "القاعدة 3: لا تدع الكرة تلمس الأرض."
                )
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u3_fb1", 3, "Do you ___ football?", "اختر الفعل المناسب لكرة القدم", listOf("play", "go", "do", "have"), "play", "نستخدم play مع ألعاب الكرة مثل football."),
            FillBlankItem("u3_fb2", 3, "Yes, I ___ rowing on Saturday.", "اختر الفعل المناسب لـ rowing", listOf("go", "play", "do", "see"), "go", "rowing تنتهي بـ ing فنستخدم معها go."),
            FillBlankItem("u3_fb3", 3, "Yes, I ___ karate on Wednesday.", "اختر الفعل المناسب لـ karate", listOf("do", "play", "go", "have"), "do", "نستخدم do مع الرياضات القتالية والفردية مثل karate."),
            FillBlankItem("u3_fb4", 3, "They ___ boxing at the sports club.", "تذكر الاستثناء المهم في الكتاب!", listOf("do", "go", "play", "make"), "do", "الملاكمة boxing استثناء وتأخذ do boxing (صفحة 40)."),
            FillBlankItem("u3_fb5", 3, "She doesn't like chocolate, so she ___ eats it.", "ظرف التكرار لـ 0%", listOf("never", "always", "often", "sometimes"), "never", "لا تحب الشوكولاتة، إذاً لا تأكلها أبداً (never)."),
            FillBlankItem("u3_fb6", 3, "Our teacher gives us homework every day. She ___ gives homework.", "ظرف التكرار لـ 100%", listOf("always", "never", "sometimes", "once"), "always", "كل يوم تعني دائماً (always).")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u3_so1", 3, "Why do we do sports?", "لماذا نمارس الرياضة؟"),
            SentenceOrderItem("u3_so2", 3, "I always clean my teeth after breakfast.", "أنا دائماً أنظف أسناني بعد الفطور."),
            SentenceOrderItem("u3_so3", 3, "How often do you help at home?", "كم مرة تساعد في المنزل؟"),
            SentenceOrderItem("u3_so4", 3, "Can you make friends in a team?", "هل يمكنك تكوين صداقات في فريق؟"),
            SentenceOrderItem("u3_so5", 3, "I go swimming three times a week.", "أذهب للسباحة ثلاث مرات في الأسبوع.")
        ),
        dialogues = listOf(
            DialogueExercise("u3_d1", 3, "Firas", "Hi! What's your favourite sport?", "ما هي رياضتك المفضلة؟", listOf("I like swimming and tennis.", "Once a year.", "At the sports club.", "No, I don't."), "I like swimming and tennis.", "نجيب بذكر الرياضة المفضلة."),
            DialogueExercise("u3_d2", 3, "Jena", "How often do you go to a museum?", "كم مرة تذهب إلى المتحف؟", listOf("I go once a year.", "I play football.", "Yes, I do.", "It's in the park."), "I go once a year.", "السؤال بـ How often نجيب عنه بعدد المرات."),
            DialogueExercise("u3_d3", 3, "Karam", "Where do you play basketball?", "أين تلعب كرة السلة؟", listOf("At the sports club.", "Twice a week.", "Yes, I do.", "I never watch TV."), "At the sports club.", "السؤال بـ Where يسأل عن المكان.")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u3_rq1", 3, "Sports are great (p.50)", "Being in a team is a good way to make friends.", "اللعب في فريق طريقة جيدة لتكوين صداقات.", listOf("Yes", "No"), "Yes", "صحيح، الفريق يساعدنا على تكوين صداقات جديدة."),
            ReadingQuestion("u3_rq2", 3, "Sports are great (p.50)", "You need to learn lots of rules to go to the water park.", "تحتاج لتعلم قواعد كثيرة للذهاب للحديقة المائية.", listOf("Yes", "No"), "No", "خطأ، القواعد الكثيرة في رياضة الكاراتيه، أما الحديقة المائية فللمرح."),
            ReadingQuestion("u3_rq3", 3, "Volleyball Project (p.53)", "How many players are in a volleyball team?", "كم لاعباً في فريق كرة الطائرة؟", listOf("Six players", "Two players", "Eleven players", "Four players"), "Six players", "يتكون فريق كرة الطائرة من 6 لاعبين (صفحة 53).")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 45,
                unitNumber = 3,
                titleEn = "Track 45: Jena and Karam talk about sports",
                titleAr = "المقطع 45: جينا وكرم يتحدثان عن الرياضة",
                scriptEn = "Jena: I like swimming and tennis. I often go swimming on Saturday at the sports club. I sometimes play tennis with my friends. Karam: I don't like swimming. I never go swimming. I like football best. I always play football at the weekend.",
                summaryAr = "تتحدث جينا عن حبها للسباحة والتنس، وكرم عن حبه لكرة القدم.",
                questionEn = "Which sport does Karam like best?",
                options = listOf("Football", "Swimming", "Tennis", "Boxing"),
                correctAnswer = "Football"
            ),
            ListeningTrack(
                trackNumber = 51,
                unitNumber = 3,
                titleEn = "Track 51: Interview with Jena and Karam",
                titleAr = "المقطع 51: مقابلة مع جينا وكرم حول الأنشطة",
                scriptEn = "Interviewer: How often do you go to a museum? Karam: We go once a year. Interviewer: How often do you play with friends? Jena: About five times a week. Interviewer: How often do you help at home? Karam: Three times a week. Interviewer: How often do you watch TV? Karam: Once a day!",
                summaryAr = "مقابلة حول عدد مرات ممارسة الأنشطة الأسبوعية واليومية.",
                questionEn = "How often do they help at home?",
                options = listOf("Three times a week", "Once a year", "Five times a week", "Never"),
                correctAnswer = "Three times a week"
            )
        )
    )

    val unit4 = BookUnit(
        number = 4,
        titleEn = "Revision (Units 1–3)",
        titleAr = "مراجعة الوحدات الأولى (1–3)",
        icon = "🏆",
        startPage = 54,
        isRevision = true,
        lessons = listOf(
            BookLesson(
                unitNumber = 4,
                lessonNumber = 1,
                titleEn = "Nour from Jordan & Animal Adjectives",
                titleAr = "نور من الأردن وصفات الحيوانات",
                pageNumber = 54,
                summaryAr = "قراءة نص نور من عمّان في الأردن (المنسف، البتراء، نهر الأردن) ومراجعة صفات الحيوانات.",
                keySentences = listOf(
                    "My name is Nour, and I'm Jordanian. I live in Amman." to "اسمي نور وأنا أردنية. أعيش في عمّان.",
                    "The national food of Jordan is mansaf." to "الطعام الوطني للأردن هو المنسف.",
                    "She thinks dolphins and horses are intelligent." to "هي تعتقد أن الدلافين والأحصنة ذكية."
                ),
                readingTitle = "Nour from Jordan (Page 54)",
                readingTextEn = "Hello. My name is Nour, and I'm Jordanian. I live in Amman. Amman is the capital city of Jordan. I speak Arabic. The national animal of Jordan is the golden jackal, and the national food is mansaf. There is the old city of Petra and the Jordan River, which is about 300 km long."
            ),
            BookLesson(
                unitNumber = 4,
                lessonNumber = 2,
                titleEn = "My pets (Rose's report)",
                titleAr = "حيواناتي الأليفة — تقرير روز",
                pageNumber = 55,
                summaryAr = "قراءة تقرير روز من إنجلترا عن طائرها Lime، وأرنبتها Cuddles، وقطة أمها Patch.",
                keySentences = listOf(
                    "Rose is from England. Her favourite pet is her bird Lime." to "روز من إنجلترا وطائرها المفضل اسمه لايم.",
                    "Cuddles is a friendly rabbit. She eats carrots and grass." to "كادلز أرنبة ودودة وتأكل الجزر والعشب.",
                    "Patch is a playful and intelligent cat." to "باتش قطة مرحة وذكية."
                ),
                readingTitle = "My pets by Rose (Page 55)",
                readingTextEn = "My name is Rose, and I'm from England. My favourite pet is my bird Lime. Lime is very gentle and eats seeds and fresh fruit. I also have a pet rabbit called Cuddles. Cuddles is very friendly and eats carrots and grass. My mum has a cat called Patch. Patch is playful and intelligent."
            ),
            BookLesson(
                unitNumber = 4,
                lessonNumber = 3,
                titleEn = "Kamal's email: My new sports club",
                titleAr = "رسالة كمال: النادي الرياضي الجديد",
                pageNumber = 56,
                summaryAr = "تصحيح الأخطاء في رسالة كمال حول النادي الرياضي والأفعال play tennis و do karate.",
                keySentences = listOf(
                    "We are very excited! There is a big swimming pool." to "نحن متحمسون جداً! يوجد مسبح كبير.",
                    "I am going to play tennis and do karate." to "سألعب التنس وأمارس الكاراتيه."
                )
            ),
            BookLesson(
                unitNumber = 4,
                lessonNumber = 4,
                titleEn = "The Faces! Revision Game",
                titleAr = "لعبة الوجوه للمراجعة الشاملة",
                pageNumber = 57,
                summaryAr = "مراجعة تفاعلية للجنسيات وظروف التكرار وصفات الحيوانات وتصحيح الأخطاء النحوية.",
                keySentences = listOf(
                    "He is from Italy. He is Italian." to "هو من إيطاليا، جنسيته إيطالي.",
                    "I always clean my teeth in the morning and at night." to "أنا دائماً أنظف أسناني صباحاً ومساءً.",
                    "She plays with her friends three times a week." to "هي تلعب مع صديقاتها ثلاث مرات أسبوعياً."
                )
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u4_fb1", 4, "I am going to ___ tennis and do karate.", "تصحيح رسالة كمال (صفحة 56)", listOf("play", "do", "go", "have"), "play", "نقول play tennis وليس do tennis."),
            FillBlankItem("u4_fb2", 4, "She ___ with her friends three times a week.", "تصحيح جملة لعبة الوجوه (صفحة 57)", listOf("plays", "play", "playing", "go"), "plays", "مع She نضيف s للفعل فتصبح plays."),
            FillBlankItem("u4_fb3", 4, "I ___ clean my teeth in the morning.", "ترتيب ظرف التكرار قبل الفعل", listOf("always", "is", "am", "does"), "always", "ظرف التكرار always يأتي قبل الفعل الرئيسي clean.")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u4_so1", 4, "Amman is the capital city of Jordan.", "عمّان هي عاصمة الأردن."),
            SentenceOrderItem("u4_so2", 4, "I always clean my teeth in the morning.", "أنا دائماً أنظف أسناني في الصباح."),
            SentenceOrderItem("u4_so3", 4, "A shark is very dangerous.", "سمك القرش خطير جداً.")
        ),
        dialogues = listOf(
            DialogueExercise("u4_d1", 4, "Teacher Lina", "He is from Italy. What's his nationality?", "هو من إيطاليا، ما جنسيته؟", listOf("He is Italian.", "He is Iraqi.", "He is French.", "He is Japanese."), "He is Italian.", "من إيطاليا -> Italian.")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u4_rq1", 4, "Nour from Jordan (p.54)", "What is the capital city of Jordan?", "ما هي عاصمة الأردن؟", listOf("Amman", "Baghdad", "Beirut", "Cairo"), "Amman", "تعيش نور في عمّان عاصمة الأردن."),
            ReadingQuestion("u4_rq2", 4, "My pets (p.55)", "Is Cuddles a cat?", "هل كادلز قطة؟", listOf("Yes", "No"), "No", "كادلز أرنبة (rabbit)، أما القطة فاسمها Patch."),
            ReadingQuestion("u4_rq3", 4, "My pets (p.55)", "Does Cuddles like carrots?", "هل تحب كادلز الجزر؟", listOf("Yes", "No"), "Yes", "روز تطعم كادلز الجزر والعشب كل يوم.")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 56,
                unitNumber = 4,
                titleEn = "Track 56: Kamal's new sports club",
                titleAr = "المقطع 56: نادي كمال الرياضي الجديد",
                scriptEn = "Yesterday, my mum and dad took me, my brother and sister to a new sports club. We are very excited! There is a big swimming pool and a beautiful park. We always go there on Saturday. I am going to play tennis and do karate. I bought a hat, and my sister bought a tennis racket.",
                summaryAr = "النسخة الصحيحة من رسالة كمال عن ناديه الرياضي الجديد.",
                questionEn = "What did Kamal's sister buy at the club shop?",
                options = listOf("A tennis racket", "A football", "Sweets", "A hat"),
                correctAnswer = "A tennis racket"
            ),
            ListeningTrack(
                trackNumber = 57,
                unitNumber = 4,
                titleEn = "Track 57: Mahmoud's routine",
                titleAr = "المقطع 57: روتين محمود اليومي",
                scriptEn = "Hello, my name is Mahmoud. I always clean my teeth after breakfast and before I go to bed. I wake up at six o'clock on school days. My favourite lesson is Art. On Wednesday after school, I go swimming at the sports club. On Saturday afternoon, I play football with my friends.",
                summaryAr = "يتحدث محمود عن يومه الدراسي ودرسه المفضل وهواياته.",
                questionEn = "What is Mahmoud's favourite lesson at school?",
                options = listOf("Art", "English", "Science", "Sport"),
                correctAnswer = "Art"
            )
        )
    )

    val all: List<BookUnit> = listOf(unit1, unit2, unit3, unit4)
}
