package com.example.data

object CurriculumUnits5to8 {

    val unit5 = BookUnit(
        number = 5,
        titleEn = "Food and drink",
        titleAr = "الطعام والشراب والمعدود وغير المعدود",
        icon = "🍎",
        startPage = 58,
        lessons = listOf(
            BookLesson(
                unitNumber = 5,
                lessonNumber = 1,
                titleEn = "Food and drink",
                titleAr = "الطعام والشراب",
                pageNumber = 58,
                summaryAr = "مفردات الطعام والشراب والسؤال بـ Do you like ...? و Are there any / Is there any.",
                keySentences = listOf(
                    "Do you like fish? Yes, I do. / No, I don't." to "هل تحب السمك؟ نعم / لا.",
                    "Are there any apples? Yes, there are lots." to "هل يوجد أي تفاح؟ نعم، يوجد الكثير.",
                    "Is there any bread? Yes, there is a lot." to "هل يوجد أي خبز؟ نعم، يوجد الكثير."
                )
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 2,
                titleEn = "What can you count?",
                titleAr = "المعدود وغير المعدود والحاويات",
                pageNumber = 60,
                summaryAr = "التمييز بين المعدود (apples, cookies, strawberries, cherries) وغير المعدود (rice, bread, salad, water, milk, tea) واستخدام الحاويات (bottle, box, cup, tin, packet, bag).",
                keySentences = listOf(
                    "A box of chocolates / A bottle of water." to "صندوق شوكولاتة / قنينة ماء.",
                    "A cup of tea / A tin of lemonade." to "كوب شاي / علبة عصير ليمون.",
                    "You can't count lemonade, but you can count tins of lemonade." to "لا يمكنك عدّ الليموناضة، لكن يمكنك عدّ علب الليموناضة."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Countable & Uncountable",
                    titleAr = "الأسماء المعدودة وغير المعدودة",
                    explanationAr = "مع الأسماء المعدودة الجمع نستخدم Are there any ...? ومع غير المعدودة نستخدم Is there any ...? ويمكن جعل غير المعدود معدوداً بإضافة حاوية (cup, bottle, box, tin, bag).",
                    examples = listOf(
                        "Are there any apples? -> Yes, there are. / No, there aren't." to "معدود (جمع)",
                        "Is there any bread/salad? -> Yes, there is. / No, there isn't." to "غير معدود"
                    )
                )
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 3,
                titleEn = "Would you like ...?",
                titleAr = "هل ترغب بـ...؟ العرض المهذب",
                pageNumber = 62,
                summaryAr = "تقديم الطعام والشراب بأدب باستخدام Would you like ...? والرد بـ Yes, please / No, thank you / I'd prefer tea, please.",
                keySentences = listOf(
                    "Would you like some salad? Yes, please." to "هل ترغب ببعض السلطة؟ نعم، من فضلك.",
                    "Would you like some lemonade or tea? I'd prefer tea, please." to "هل تفضل عصير الليمون أم الشاي؟ أفضل الشاي، من فضلك.",
                    "Would you like some bread? No, thank you." to "هل ترغب ببعض الخبز؟ لا، شكراً لك."
                )
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 4,
                titleEn = "Are you healthy?",
                titleAr = "هل أنت صحي؟",
                pageNumber = 64,
                summaryAr = "العادات الصحية (eat fruit and vegetables, sleep 9 hours, drink enough water, exercise) وغير الصحية (lots of sweets, cola, late TV).",
                keySentences = listOf(
                    "Drink lots of water. -> Healthy!" to "اشرب الكثير من الماء -> صحي!",
                    "Eat lots of fruit and vegetables. -> Healthy!" to "تناول الكثير من الفواكه والخضروات -> صحي!",
                    "Eat lots of sweets and biscuits. -> Unhealthy!" to "تناول الكثير من الحلويات والبسكويت -> غير صحي!"
                )
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 5,
                titleEn = "My lunch box",
                titleAr = "صندوق غدائي",
                pageNumber = 66,
                summaryAr = "مقارنة صندوق الغداء الصحي (cucumber, milk, apple) مع غير الصحي (burger, cola, sweets) واستخدام there is an / there are some / there is some.",
                keySentences = listOf(
                    "There is an apple. / There are some apples." to "للمعدود المفرد والجمع.",
                    "There is some milk. / There is some cheese." to "لغير المعدود نستخدم there is some."
                )
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 6,
                titleEn = "My Magazine: Picnic by the lake",
                titleAr = "مجلتي: نزهة قرب البحيرة",
                pageNumber = 68,
                summaryAr = "قصة عائلة أمل وطارق في نزهة قرب البحيرة: اصطاد الأب سمكة كبيرة، جمع الأطفال الحطب، وشووا السمكة اللذيذة.",
                keySentences = listOf(
                    "The family went on a picnic to a lake by car." to "ذهبت العائلة في نزهة إلى البحيرة بالسيارة.",
                    "Dad caught a big fish in the lake." to "اصطاد الأب سمكة كبيرة من البحيرة.",
                    "The children collected sticks of wood and Dad made a fire." to "جمع الأطفال عيدان الحطب وأشعل الأب النار."
                ),
                readingTitle = "Picnic by the lake (Pages 68-69)",
                readingTextEn = "The family is going on a picnic to a lake by car. Dad is driving, and Mum is next to Dad. Amal and Tariq are sitting in the back. They find a big tree to sit under and unpack the picnic bag. Dad goes fishing and catches a very big fish! The children collect sticks of wood, Dad makes a fire, and they cook the delicious fish."
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 7,
                titleEn = "Check my understanding: Story order",
                titleAr = "ترتيب أحداث قصة النزهة",
                pageNumber = 70,
                summaryAr = "مراجعة الزمن الماضي لأحداث القصة (went, got into, drove, unpacked, caught, found, cooked, ate).",
                keySentences = listOf(
                    "First, they unpacked the picnic bag." to "أولاً، أفرغوا حقيبة النزهة.",
                    "They ate the fish and all the picnic food. It was a great day!" to "أكلوا السمكة وكل طعام النزهة. كان يوماً رائعاً!"
                )
            ),
            BookLesson(
                unitNumber = 5,
                lessonNumber = 8,
                titleEn = "Let's sing! Grapes and apples & Picnic poster",
                titleAr = "أنشودة العنب والتفاح وملصق النزهة",
                pageNumber = 72,
                summaryAr = "أنشودة الطعام اللذيذ وملصق نزهة منتزه الزوراء (Al-Zawraa Park).",
                keySentences = listOf(
                    "Grapes and apples, salad and soup, dates and pears." to "عنب وتفاح، سلطة وحساء، تمر وكمثرى.",
                    "Please bring healthy food to Al-Zawraa Park!" to "يرجى إحضار طعام صحي إلى منتزه الزوراء!"
                ),
                readingTitle = "Rhyme: Grapes and apples (Page 72)",
                readingTextEn = "Grapes and apples, salad and soup, dates and pears, bread and cherries, chicken and cheese, meat and rice. All these things are very nice! Are there any lemons for my tea? Are there any cakes just for me? Is there any ice cream? It's so hot! Are there any eggs? Is there any fish? Please put these things in a dish!"
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u5_fb1", 5, "___ there any apples?", "التفاح اسم معدود جمع", listOf("Are", "Is", "Do", "Does"), "Are", "التفاح جمع معدود، فنستخدم Are there any."),
            FillBlankItem("u5_fb2", 5, "___ there any bread?", "الخبز اسم غير معدود", listOf("Is", "Are", "Do", "Have"), "Is", "الخبز غير معدود، فنستخدم Is there any."),
            FillBlankItem("u5_fb3", 5, "A ___ of water.", "اختر الحاوية المناسبة للماء", listOf("bottle", "box", "bag", "plate"), "bottle", "قنينة ماء: a bottle of water."),
            FillBlankItem("u5_fb4", 5, "A ___ of tea.", "اختر الحاوية المناسبة للشاي", listOf("cup", "box", "bag", "tin"), "cup", "كوب شاي: a cup of tea."),
            FillBlankItem("u5_fb5", 5, "Would you like tea or lemonade? I'd ___ tea, please.", "التعبير عن التفضيل", listOf("prefer", "play", "go", "count"), "prefer", "نستخدم I'd prefer للتعبير عن التفضيل بأدب."),
            FillBlankItem("u5_fb6", 5, "There is ___ cheese in the lunch box.", "الجبن غير معدود", listOf("some", "a", "an", "many"), "some", "مع غير المعدود نستخدم there is some وليس a.")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u5_so1", 5, "Are there any strawberries?", "هل توجد أي فراولة؟"),
            SentenceOrderItem("u5_so2", 5, "Would you like some salad?", "هل ترغب ببعض السلطة؟"),
            SentenceOrderItem("u5_so3", 5, "Can I have two bags of rice?", "هل يمكنني الحصول على كيسين من الأرز؟"),
            SentenceOrderItem("u5_so4", 5, "Dad caught a big fish.", "اصطاد أبي سمكة كبيرة."),
            SentenceOrderItem("u5_so5", 5, "There isn't any ice cream.", "لا يوجد أي آيس كريم.")
        ),
        dialogues = listOf(
            DialogueExercise("u5_d1", 5, "Grandmother", "Would you like some bread?", "هل ترغب ببعض الخبز؟", listOf("Yes, please.", "Yes, there is.", "No, I don't.", "It's a bottle."), "Yes, please.", "عند قبول العرض بـ Would you like نقول Yes, please."),
            DialogueExercise("u5_d2", 5, "Dima", "Would you like lemonade or tea?", "هل تفضل الليموناضة أم الشاي؟", listOf("I'd prefer tea, please.", "Yes, there are.", "No, it isn't.", "Once a day."), "I'd prefer tea, please.", "عند الاختيار بين شيئين نستخدم I'd prefer ... please."),
            DialogueExercise("u5_d3", 5, "Karam", "Is there any salad in the fridge?", "هل يوجد سلطة في الثلاجة؟", listOf("No, there isn't any salad.", "No, there aren't.", "Yes, I do.", "I'd prefer water."), "No, there isn't any salad.", "السلطة غير معدودة فنستخدم isn't.")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u5_rq1", 5, "Are you healthy? (p.65)", "Which habit is HEALTHY?", "أي عادة من هذه العادات صحية؟", listOf("Drink lots of water", "Play computer games at night", "Drink lots of cola", "Don't have breakfast"), "Drink lots of water", "شرب الكثير من الماء عادة صحية جداً!"),
            ReadingQuestion("u5_rq2", 5, "Picnic by the lake (p.68)", "How did the family go to the lake?", "كيف ذهبت العائلة إلى البحيرة؟", listOf("By car", "By plane", "By train", "On foot"), "By car", "ذهبت العائلة بالسيارة وكان الأب يقود."),
            ReadingQuestion("u5_rq3", 5, "Picnic by the lake (p.69)", "What did Dad catch in the lake?", "ماذا اصطاد الأب في البحيرة؟", listOf("A big fish", "A duck", "A shark", "A frog"), "A big fish", "اصطاد الأب سمكة كبيرة وطبخوها على النار.")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 59,
                unitNumber = 5,
                titleEn = "Track 59: Food for our picnic",
                titleAr = "المقطع 59: طعام النزهة (معدود وغير معدود)",
                scriptEn = "Are there any apples? Yes, there are lots. Are there any strawberries? Yes, there are lots. Are there any cookies? No, there aren't. Is there any bread? Yes, there is a lot. Is there any salad? No, there isn't. Is there any rice? Yes, there is a lot.",
                summaryAr = "أسئلة وأجوبة عن وجود أطعمة مختلفة للنزهة.",
                questionEn = "Is there any salad for the picnic?",
                options = listOf("No, there isn't", "Yes, there is a lot", "Yes, there are lots", "No, there aren't"),
                correctAnswer = "No, there isn't"
            ),
            ListeningTrack(
                trackNumber = 64,
                unitNumber = 5,
                titleEn = "Track 64: Dima and Firas plan a picnic",
                titleAr = "المقطع 64: ديما وفراس يخططان للنزهة",
                scriptEn = "Dima: Would you like to come to a picnic on Saturday? Firas: Yes, please. Dima: Great! Would you like salad or sandwiches? Firas: I'd prefer sandwiches, please. Dima: Would you like to drink tea, coffee or lemonade? Firas: I'd like some tea, please.",
                summaryAr = "محادثة مهذبة بين ديما وفراس حول اختيار طعام وشراب النزهة.",
                questionEn = "What would Firas prefer to eat?",
                options = listOf("Sandwiches", "Salad", "Cookies", "Fish"),
                correctAnswer = "Sandwiches"
            )
        )
    )

    val unit6 = BookUnit(
        number = 6,
        titleEn = "Where is it?",
        titleAr = "أين هو؟ — المتاجر والاتجاهات ومدينة الألعاب",
        icon = "🏪",
        startPage = 74,
        lessons = listOf(
            BookLesson(
                unitNumber = 6,
                lessonNumber = 1,
                titleEn = "In the capital city",
                titleAr = "في العاصمة: المتاجر وحروف المكان",
                pageNumber = 74,
                summaryAr = "أسماء المتاجر (bakery, toy shop, pharmacy, bank, fruit and vegetable stall, butcher's, clothes shop, shoe shop) وحروف المكان (opposite, next to, between, behind).",
                keySentences = listOf(
                    "You get bread from the bakery." to "تحصل على الخبز من المخبز.",
                    "You get meat from the butcher's." to "تحصل على اللحم من محل القصاب.",
                    "You get money from the bank." to "تحصل على المال من المصرف."
                )
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 2,
                titleEn = "Excuse me. Where's ...?",
                titleAr = "عذراً، أين يقع...؟ السؤال عن الاتجاهات",
                pageNumber = 76,
                summaryAr = "وصف مواقع المحلات على خريطة الشوارع (Green Street, Red Street, Blue Street).",
                keySentences = listOf(
                    "Excuse me. I'm lost. Where's the shoe shop?" to "عذراً، أنا تائه. أين محل الأحذية؟",
                    "It's on Green Street between the clothes shop and the bakery." to "إنه في الشارع الأخضر بين محل الملابس والمخبز.",
                    "It's on Red Street opposite the butcher's." to "إنه في الشارع الأحمر مقابل محل القصاب.",
                    "The mosque is behind the park." to "المسجد خلف المنتزه."
                )
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 3,
                titleEn = "Helping Mum (was / were)",
                titleAr = "مساعدة الأم — الماضي البسيط (was / were)",
                pageNumber = 78,
                summaryAr = "التحدث عن الأماكن في الماضي (Where was Rahaf at 11:30? Where were you yesterday?).",
                keySentences = listOf(
                    "At 10:30, Mum was at the pharmacy." to "الساعة 10:30 كانت الأم في الصيدلية.",
                    "At 10:45, Rahaf and Ameer were at the toy shop." to "الساعة 10:45 كان رهف وأمير في محل الألعاب.",
                    "Where were you at nine o'clock yesterday morning? I was at school." to "أين كنت الساعة 9 صباح أمس؟ كنت في المدرسة."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Was / Were",
                    titleAr = "الماضي من verb to be (was / were)",
                    explanationAr = "نستخدم was مع المفرد (I, he, she, it) ونستخدم were مع الجمع والمخاطب (you, we, they).",
                    examples = listOf(
                        "Where was Rahaf? -> She was at the park." to "للمفرد نستخدم was",
                        "Where were you yesterday? -> I was at home." to "مع you نستخدم were ومع I نستخدم was"
                    )
                )
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 4,
                titleEn = "At Fun City",
                titleAr = "في مدينة الألعاب",
                pageNumber = 80,
                summaryAr = "ألعاب مدينة الملاهي في أربيل (big wheel, roller coaster, super swing, bumper cars, clowns, super slide, karting, merry-go-round) والعرض بـ Would you like to go on...?",
                keySentences = listOf(
                    "Would you like to go on the big wheel? Yes, I would. / No, I wouldn't." to "هل تود ركوب دولاب الهواء؟",
                    "Would you like to go karting? No, thank you. It's dangerous." to "هل تود ركوب سيارات السباق؟ لا شكراً، إنها خطيرة.",
                    "Would you like to see the clowns? Yes, I would." to "هل تود رؤية المهرجين؟ نعم أود ذلك."
                )
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 5,
                titleEn = "Souvenirs",
                titleAr = "الهدايا التذكارية",
                pageNumber = 82,
                summaryAr = "التعرف على الهدايا التذكارية (postcard, mug, T-shirt, key ring, baseball cap, doll) وبرج إيفل في فرنسا.",
                keySentences = listOf(
                    "Souvenirs are little things that help you remember a place you visited." to "الهدايا التذكارية أشياء صغيرة تذكرك بالمكان الذي زرته.",
                    "The Eiffel Tower is in France." to "برج إيفل يقع في فرنسا."
                )
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 6,
                titleEn = "My Magazine: A fun day out",
                titleAr = "مجلتي: يوم ممتع في الخارج",
                pageNumber = 84,
                summaryAr = "ثلاث وجهات ترفيهية: Fun City (تغلق الإثنين، ألعاب صغار وهدايا)، Water World (3 مسابح ومنزلقات مائية)، Magic Castles (الصيف فقط، شلال سحري وأغلى تذكرة).",
                keySentences = listOf(
                    "Fun City is open every day except Monday." to "مدينة المرح مفتوحة كل يوم ما عدا الإثنين.",
                    "You can swim at Water World." to "يمكنك السباحة في عالم المياه.",
                    "Magic Castles is open in summer only and is the most expensive." to "القلاع السحرية مفتوحة في الصيف فقط وهي الأغلى."
                ),
                readingTitle = "A fun day out (Pages 84-85)",
                readingTextEn = "Fun City has roller coasters, the big wheel, bumper cars, baby rides and souvenirs. It is open every day except Monday. Water World has 3 pools, water slides, swim with fish and a café. Magic Castles is open in summer only, Thursday to Tuesday, with a magic waterfall, birds, butterflies and fruit juice. Adults: 9,000 IQD."
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 7,
                titleEn = "Check my understanding: Postcards",
                titleAr = "البطاقات البريدية من بابل ووينترلاند",
                pageNumber = 86,
                summaryAr = "ترتيب جمل بطاقة بابل ومطابقة بطاقات زينب ونور وباسم.",
                keySentences = listOf(
                    "This is a postcard from Babil, in Iraq! We visited the ruins of Babylon." to "هذه بطاقة بريدية من بابل في العراق! زرنا آثار بابل.",
                    "Did Basim go fishing? No, he didn't." to "هل ذهب باسم للصيد؟ لا، لم يفعل."
                )
            ),
            BookLesson(
                unitNumber = 6,
                lessonNumber = 8,
                titleEn = "Story: The shopping list",
                titleAr = "قصة قائمة التسوق",
                pageNumber = 88,
                summaryAr = "قصة سامي المضحكة حين أضاع قائمة التسوق (bag of rice, big cake, red pen, 12 eggs) وحوّلها وهو يمر بحديقة الحيوانات إلى (big snake و red hen)!",
                keySentences = listOf(
                    "Mum gave Sami a shopping list: a bag of rice, a big cake, a red pen and 12 eggs." to "أعطت الأم سامي قائمة تسوق: كيس أرز وكعكة كبيرة وقلم أحمر و12 بيضة.",
                    "On the way, Sami lost the shopping list!" to "في الطريق أضاع سامي قائمة التسوق!",
                    "Sami bought a big snake and a red hen. Mum was not happy!" to "اشترى سامي أفعى كبيرة ودجاجة حمراء ولم تكن الأم سعيدة!"
                ),
                readingTitle = "The shopping list (Pages 88-89)",
                readingTextEn = "Mum asked Sami to go to the shop with a list: a bag of rice, a big cake, a red pen and 12 eggs. On the way, Sami lost the list! Walking past the zoo, cake became a big snake. Walking past Grandma's chickens, red pen became a red hen! Sami took the box home and Mum was not happy!"
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u6_fb1", 6, "Where can you get bread from? From the ___.", "من أين نشتري الخبز؟", listOf("bakery", "pharmacy", "bank", "butcher's"), "bakery", "نشتري الخبز من المخبز bakery."),
            FillBlankItem("u6_fb2", 6, "The shoe shop is ___ the clothes shop and the bakery.", "حرف الجر (بين)", listOf("between", "behind", "opposite", "in"), "between", "عندما نذكر مكانين (and) نستخدم between."),
            FillBlankItem("u6_fb3", 6, "At 10:30, Mum ___ at the pharmacy.", "الماضي مع المفرد Mum", listOf("was", "were", "is", "are"), "was", "الأم مفرد وفي الماضي نستخدم was."),
            FillBlankItem("u6_fb4", 6, "Where ___ you at nine o'clock yesterday morning?", "الماضي مع الضمير you", listOf("were", "was", "are", "did"), "were", "نستخدم were مع you في الماضي."),
            FillBlankItem("u6_fb5", 6, "Would you like to ___ on the big wheel?", "التركيب الصحيح لركوب الألعاب", listOf("go", "play", "do", "buy"), "go", "نقول go on the big wheel / roller coaster."),
            FillBlankItem("u6_fb6", 6, "The Eiffel Tower is in ___.", "من درس الهدايا التذكارية صفحة 82", listOf("France", "Italy", "Thailand", "Egypt"), "France", "برج إيفل في فرنسا France.")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u6_so1", 6, "Excuse me. Where's the toy shop?", "عذراً، أين محل الألعاب؟"),
            SentenceOrderItem("u6_so2", 6, "It's on Red Street opposite the butcher's.", "إنه في الشارع الأحمر مقابل القصاب."),
            SentenceOrderItem("u6_so3", 6, "Where was Rahaf at 11:30?", "أين كانت رهف الساعة 11:30؟"),
            SentenceOrderItem("u6_so4", 6, "Would you like to see the clowns?", "هل تود رؤية المهرجين؟"),
            SentenceOrderItem("u6_so5", 6, "Sami lost the shopping list.", "أضاع سامي قائمة التسوق.")
        ),
        dialogues = listOf(
            DialogueExercise("u6_d1", 6, "Boy", "I want to buy some bread.", "أريد شراء بعض الخبز.", listOf("Then go to the bakery on Green Street.", "Then go to the pharmacy.", "Yes, I would.", "It's behind the zoo."), "Then go to the bakery on Green Street.", "الخبز يُشترى من المخبز bakery."),
            DialogueExercise("u6_d2", 6, "Firas", "Would you like to go karting?", "هل تود ركوب سيارات السباق؟", listOf("No, thank you. It's dangerous.", "At the butcher's.", "Yes, there are.", "I was at home."), "No, thank you. It's dangerous.", "إجابة مهذبة من جدول صفحة 81."),
            DialogueExercise("u6_d3", 6, "Teacher", "Where were you at 9 o'clock yesterday?", "أين كنت الساعة التاسعة أمس؟", listOf("I was at school.", "I am Iraqi.", "Yes, she did.", "It's next to the bank."), "I was at school.", "السؤال في الماضي بـ Where were you نجيب عنه بـ I was at...")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u6_rq1", 6, "A fun day out (p.86)", "Which place can you swim in?", "في أي مكان يمكنك السباحة؟", listOf("Water World", "Fun City", "Magic Castles", "The bakery"), "Water World", "Water World يحتوي على 3 مسابح ومنزلقات مائية."),
            ReadingQuestion("u6_rq2", 6, "A fun day out (p.86)", "Which place is closed on Monday?", "أي مكان يغلق يوم الإثنين؟", listOf("Fun City", "Water World", "Magic Castles", "The bank"), "Fun City", "Fun City مفتوحة كل يوم ما عدا الإثنين."),
            ReadingQuestion("u6_rq3", 6, "The shopping list (p.88)", "What was REALLY on Mum's shopping list?", "ماذا كان حقاً في قائمة تسوق الأم؟", listOf("A big cake and a red pen", "A big snake and a red hen", "A toy and a doll", "A fish and a mug"), "A big cake and a red pen", "سامي خلط بين cake/snake و pen/hen!")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 75,
                unitNumber = 6,
                titleEn = "Track 75: Asking for directions",
                titleAr = "المقطع 75: السؤال عن مواقع المتاجر",
                scriptEn = "Boy: Excuse me. Where's the toy shop? Man 1: It's on Green Street next to the bank. Young man: Excuse me. I'm lost. Where's the shoe shop? Man 2: It's on Green Street between the clothes shop and the bakery. Girl: Excuse me. Where's the fruit and vegetable stall? Woman: It's on Red Street opposite the butcher's.",
                summaryAr = "ثلاث محادثات قصيرة للسؤال عن مواقع المتاجر في المدينة.",
                questionEn = "Where is the toy shop?",
                options = listOf("Next to the bank", "Opposite the butcher's", "Behind the park", "In the school"),
                correctAnswer = "Next to the bank"
            ),
            ListeningTrack(
                trackNumber = 88,
                unitNumber = 6,
                titleEn = "Track 88: Story — The shopping list",
                titleAr = "المقطع 88: قصة قائمة التسوق لسامي",
                scriptEn = "Mum was very busy. She gave Sami a shopping list: a bag of rice, a big cake, a red pen and 12 eggs. On the way, Sami lost the shopping list! He walked past the zoo and said: a big snake. He walked past Grandma's chickens and said: a red hen! Sami took the box home. Mum was not happy!",
                summaryAr = "استمع لقصة سامي الكوميدية مع قائمة التسوق.",
                questionEn = "Why was Mum not happy at the end?",
                options = listOf("Sami bought a snake and a hen", "The shop was closed", "Sami forgot the rice", "It was rainy"),
                correctAnswer = "Sami bought a snake and a hen"
            )
        )
    )

    val unit7 = BookUnit(
        number = 7,
        titleEn = "Seasons and holidays",
        titleAr = "الفصول والطقس والعطلات والسفر",
        icon = "🌦️",
        startPage = 90,
        lessons = listOf(
            BookLesson(
                unitNumber = 7,
                lessonNumber = 1,
                titleEn = "The seasons",
                titleAr = "فصول السنة الأربعة والطقس",
                pageNumber = 90,
                summaryAr = "فصول السنة وأشهرها في العراق: Winter (Dec-Feb: cold, rainy, cloudy)، Spring (Mar-Apr: cool, rainy, mild)، Summer (May-Aug: sunny, hot, dry)، Autumn (Sep-Nov: warm, windy).",
                keySentences = listOf(
                    "What's the weather like in spring? It's cool, rainy and mild." to "كيف الطقس في الربيع؟ معتدل وممطر ولطيف.",
                    "In summer, it's sunny, hot and dry." to "في الصيف يكون مشمساً وحاراً وجافاً.",
                    "In winter, it's cold, rainy and cloudy." to "في الشتاء يكون بارداً وممطراً وغائماً."
                )
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 2,
                titleEn = "Going on a summer holiday",
                titleAr = "الذهاب في عطلة صيفية (going to)",
                pageNumber = 92,
                summaryAr = "التخطيط للمستقبل باستخدام (am/is/are + going to + فعل مجرد).",
                keySentences = listOf(
                    "I'm going to go on holiday to Egypt with my family." to "سأذهب في عطلة إلى مصر مع عائلتي.",
                    "We're going to visit the pyramids." to "سنزور الأهرامات.",
                    "I'm going to ride a camel in the desert." to "سأركب جملاً في الصحراء."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Let's check! Future with going to",
                    titleAr = "المستقبل القريب باستخدام going to",
                    explanationAr = "للتحدث عن خططنا للعطلة نستخدم: I'm / You're / He's / She's / We're / They're + going to + فعل مجرد (go, see, learn, take, buy).",
                    examples = listOf(
                        "We're going to fly there by plane." to "سنسافر إلى هناك بالطائرة",
                        "I'm going to buy souvenirs for my friends." to "سأشتري هدايا تذكارية لأصدقائي"
                    )
                )
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 3,
                titleEn = "At the airport (Possessive pronouns)",
                titleAr = "في المطار وضمائر التملك",
                pageNumber = 94,
                summaryAr = "السؤال بـ Whose والتحويل بين صفات التملك وضمائر التملك (my->mine, your->yours, her->hers, his->his).",
                keySentences = listOf(
                    "Whose big, black bag is this? It's mine." to "لمن هذه الحقيبة السوداء الكبيرة؟ إنها لي.",
                    "It's your book. -> It's yours." to "إنه كتابك -> إنه لك.",
                    "It's her pencil case. -> It's hers." to "إنها محفظتها -> إنها لها.",
                    "They are his pens. -> They're his." to "إنها أقلامه -> إنها له."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Possessive Adjectives -> Possessive Pronouns",
                    titleAr = "ضمائر التملك (mine / yours / hers / his)",
                    explanationAr = "صفات التملك يتبعها اسم (my bag)، أما ضمائر التملك فتأتي في نهاية الجملة بدون اسم (It's mine).",
                    examples = listOf(
                        "my bag -> mine | your book -> yours" to "حقيبتي -> لي | كتابك -> لك",
                        "her case -> hers | his pens -> his" to "محفظتها -> لها | أقلامه -> له"
                    )
                )
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 4,
                titleEn = "Presents for everyone",
                titleAr = "هدايا للجميع",
                pageNumber = 96,
                summaryAr = "هدايا نور من مصر لعائلتها (key ring to dad, chocolates to grandmother, sweets to brother/sister, scarf to mum, crayons to friend).",
                keySentences = listOf(
                    "I'm going to give the chocolates to my grandmother." to "سأعطي الشوكولاتة لجدتي.",
                    "I bought you some flowers. Thank you, they're lovely!" to "اشتريت لكِ بعض الأزهار. شكراً، إنها جميلة!"
                )
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 5,
                titleEn = "Holiday weather & Holidays in Iraq",
                titleAr = "طقس العطلة وعطلات في العراق",
                pageNumber = 98,
                summaryAr = "التحدث عن العطلات الماضية في البصرة (حار ورطب)، الأهوار (معتدل ولطيف)، وجبال الشمال (بارد ومثلج).",
                keySentences = listOf(
                    "Last summer, we went to visit our grandparents in Basra. It was hot and humid." to "الصيف الماضي زرنا أجدادنا في البصرة. كان الطقس حاراً ورطباً.",
                    "Last spring, we went to the marshes in the south of Iraq." to "الربيع الماضي ذهبنا إلى الأهوار في جنوب العراق.",
                    "For our mid-year holiday, we went to the mountains in the north of Iraq." to "في عطلة نصف السنة ذهبنا إلى الجبال في شمال العراق."
                )
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 6,
                titleEn = "My Magazine: How we travel",
                titleAr = "مجلتي: كيف نسافر؟",
                pageNumber = 100,
                summaryAr = "وسائل السفر في العراق: الطائرة إلى النجف الأشرف (30 دقيقة من بغداد)، السيارة إلى ديالى، العبّارة في النهر، والقطار السريع إلى الناصرية.",
                keySentences = listOf(
                    "She flew to Al-Najaf Al-Ashraf by plane in 30 minutes." to "سافرت بالطائرة إلى النجف الأشرف في 30 دقيقة.",
                    "My school is going on a picnic by ferry boat." to "مدرستي ذاهبة في نزهة بالعبّارة النهرية.",
                    "My brother went to Nasiriyah by train." to "ذهب أخي إلى الناصرية بالقطار."
                ),
                readingTitle = "How we travel (Page 101)",
                readingTextEn = "Dima's mother flew from Baghdad to Al-Najaf Al-Ashraf by plane in 30 minutes. Karam's dad works in Diyala and goes by car. Jena's school is going on a picnic by ferry boat on the river. Firas's brother went by train to visit his aunt and uncle in Nasiriyah."
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 7,
                titleEn = "Check my understanding: by / on foot & Oman",
                titleAr = "قاعدة by و on foot وعطلة زهرة في عُمان",
                pageNumber = 102,
                summaryAr = "استخدام by مع وسائل النقل (by car, bus, train, boat, plane) و on مع foot فقط (on foot) + قصة زهرة في عُمان.",
                keySentences = listOf(
                    "I go to school by car / by bus / by train / by plane." to "نستخدم by مع كل وسائل النقل.",
                    "But I go to school on foot." to "لكن للسير على الأقدام نستخدم on foot."
                ),
                grammarRule = GrammarRule(
                    titleEn = "Remember! by + transport | on + foot",
                    titleAr = "حروف الجر مع وسائل النقل",
                    explanationAr = "نستخدم by مع جميع وسائل النقل (car, bus, train, boat, plane)، ونستخدم on فقط مع كلمة foot.",
                    examples = listOf(
                        "by car / by bus / by train / by boat / by plane" to "مع وسائل المواصلات",
                        "on foot" to "سيراً على الأقدام"
                    )
                )
            ),
            BookLesson(
                unitNumber = 7,
                lessonNumber = 8,
                titleEn = "Let's play! & Favourite Holiday in Jordan",
                titleAr = "هيا نلعب وملصق عطلة الأردن",
                pageNumber = 104,
                summaryAr = "مراجعة الأفعال الماضية (flew, went, rode, swam, ate) وملصق عطلة الأردن (البحر الميت، البتراء، المنسف).",
                keySentences = listOf(
                    "My favourite holiday was in Jordan. I went by plane." to "عطلتي المفضلة كانت في الأردن وسافرت بالطائرة.",
                    "I floated in the Dead Sea and saw the caves in Petra." to "طفوت في البحر الميت ورأيت كهوف البتراء."
                )
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u7_fb1", 7, "It's my bag. It's ___.", "ضمير التملك لـ my", listOf("mine", "yours", "hers", "his"), "mine", "my bag تتحول إلى ضمير التملك mine."),
            FillBlankItem("u7_fb2", 7, "It's her pencil case. It's ___.", "ضمير التملك لـ her", listOf("hers", "his", "mine", "yours"), "hers", "her pencil case تتحول إلى hers."),
            FillBlankItem("u7_fb3", 7, "I go to school ___ foot.", "حرف الجر مع foot (صفحة 102)", listOf("on", "by", "in", "at"), "on", "نستخدم on فقط مع foot (on foot)."),
            FillBlankItem("u7_fb4", 7, "We flew to Egypt ___ plane.", "حرف الجر مع وسائل النقل", listOf("by", "on", "in", "at"), "by", "نستخدم by مع وسائل النقل (by plane)."),
            FillBlankItem("u7_fb5", 7, "Last summer, we ___ to visit our grandparents in Basra.", "الماضي من go", listOf("went", "go", "going", "flew"), "went", "الماضي من go هو went."),
            FillBlankItem("u7_fb6", 7, "In summer, the weather is sunny, hot and ___.", "صفة طقس الصيف", listOf("dry", "snowy", "cold", "cloudy"), "dry", "في الصيف الطقس مشمس وحار وجاف (dry).")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u7_so1", 7, "What's the weather like in spring?", "كيف يكون الطقس في الربيع؟"),
            SentenceOrderItem("u7_so2", 7, "We're going to visit the pyramids.", "سنزور الأهرامات."),
            SentenceOrderItem("u7_so3", 7, "Whose big black bag is this?", "لمن هذه الحقيبة السوداء الكبيرة؟"),
            SentenceOrderItem("u7_so4", 7, "I go to school on foot.", "أذهب إلى المدرسة سيراً على الأقدام."),
            SentenceOrderItem("u7_so5", 7, "It was sunny and very hot.", "كان الطقس مشمساً وحاراً جداً.")
        ),
        dialogues = listOf(
            DialogueExercise("u7_d1", 7, "Dima", "What's the weather like in winter?", "كيف الطقس في الشتاء؟", listOf("It's cold, rainy and cloudy.", "It's hot and dry.", "By plane.", "It's mine."), "It's cold, rainy and cloudy.", "في الشتاء يكون الطقس بارداً وممطراً وغائماً."),
            DialogueExercise("u7_d2", 7, "Karam", "Whose teddy is this?", "لمن هذا الدبدوب؟", listOf("It's hers.", "By train.", "In March.", "Yes, I would."), "It's hers.", "السؤال بـ Whose يسأل عن الملكية فنجيب بـ It's hers/his/mine."),
            DialogueExercise("u7_d3", 7, "Jena", "How are you going to get there?", "كيف ستصلون إلى هناك؟", listOf("We're going to go there by plane.", "With my family.", "In Basra.", "It's sunny."), "We're going to go there by plane.", "السؤال بـ How يسأل عن وسيلة السفر.")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u7_rq1", 7, "How we travel (p.102)", "How long does it take to fly from Baghdad to Al-Najaf Al-Ashraf?", "كم تستغرق الرحلة بالطائرة من بغداد إلى النجف؟", listOf("30 minutes", "Two hours", "Four hours", "One day"), "30 minutes", "تستغرق 30 دقيقة بالطائرة وساعتين بالسيارة."),
            ReadingQuestion("u7_rq2", 7, "How we travel (p.102)", "Where do Firas's aunt and uncle live?", "أين يعيش عم وخالة فراس؟", listOf("In Nasiriyah", "In Basra", "In Diyala", "In Erbil"), "In Nasiriyah", "سافر أخو فراس بالقطار إلى الناصرية (Nasiriyah)."),
            ReadingQuestion("u7_rq3", 7, "Holidays in Iraq (p.99)", "What was the weather like in Basra in summer?", "كيف كان الطقس في البصرة صيفاً؟", listOf("Hot and humid", "Cold and snowy", "Cool and mild", "Windy and cold"), "Hot and humid", "كان الطقس في البصرة حاراً ورطباً (hot and humid).")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 89,
                unitNumber = 7,
                titleEn = "Track 89: The four seasons in Iraq",
                titleAr = "المقطع 89: الفصول الأربعة في العراق",
                scriptEn = "Karam: It's cold and snowy in winter. In Iraq, the winter months are December, January and February. Jena: I love spring! The weather is cool and mild, and there are flowers. Spring months are March and April. Karam: Summer is hot and sunny: May, June, July and August. Jena: Autumn is warm: September, October and November.",
                summaryAr = "يتحدث كرم وجينا عن الفصول الأربعة وأشهرها في العراق.",
                questionEn = "Which months are spring in Iraq?",
                options = listOf("March and April", "December and January", "May and June", "September and October"),
                correctAnswer = "March and April"
            ),
            ListeningTrack(
                trackNumber = 94,
                unitNumber = 7,
                titleEn = "Track 94: At the airport",
                titleAr = "المقطع 94: في المطار وحقائب السفر",
                scriptEn = "Dad: That's our flight to Egypt. Check-in assistant: Whose big, black bag is this? It weighs 30 kilograms. Mum: It's mine. Girl: The red bag is mine. Boy: Mine is green. Little girl: My teddy's bag is blue and white.",
                summaryAr = "محادثة العائلة في المطار قبل السفر إلى مصر.",
                questionEn = "Whose big, black bag weighs 30 kilograms?",
                options = listOf("Mum's (mine)", "The boy's", "The teddy's", "The assistant's"),
                correctAnswer = "Mum's (mine)"
            )
        )
    )

    val unit8 = BookUnit(
        number = 8,
        titleEn = "Revision (Units 5–7)",
        titleAr = "المراجعة النهائية الشاملة للكتاب",
        icon = "🎓",
        startPage = 106,
        isRevision = true,
        lessons = listOf(
            BookLesson(
                unitNumber = 8,
                lessonNumber = 1,
                titleEn = "Food revision & Would you like...?",
                titleAr = "مراجعة الطعام والمعدود وغير المعدود",
                pageNumber = 106,
                summaryAr = "مراجعة الأطعمة المعدودة وغير المعدودة وتكوين أسئلة العرض (Would you like some eggs / fish / apples?).",
                keySentences = listOf(
                    "Would you like some eggs? No, thank you. I prefer fruit." to "هل ترغب ببعض البيض؟ لا شكراً، أفضل الفاكهة.",
                    "Would you like some fish? Thank you. I love fish!" to "هل ترغب ببعض السمك؟ شكراً، أحب السمك!"
                )
            ),
            BookLesson(
                unitNumber = 8,
                lessonNumber = 2,
                titleEn = "Town map & Where was Firas?",
                titleAr = "مراجعة خريطة المدينة والماضي البسيط",
                pageNumber = 107,
                summaryAr = "مراجعة حروف المكان (next to, opposite, between, behind) والسؤال عن أماكن فراس في الماضي (Where was Firas at 2 o'clock?).",
                keySentences = listOf(
                    "The pharmacy is between the bank and the toy shop." to "الصيدلية بين المصرف ومحل الألعاب.",
                    "The park is behind the bakery." to "المنتزه خلف المخبز.",
                    "Where was Firas at 2 o'clock? He was at the shoe shop." to "أين كان فراس الساعة الثانية؟ كان في محل الأحذية."
                )
            ),
            BookLesson(
                unitNumber = 8,
                lessonNumber = 3,
                titleEn = "Jena's planner & Possessive pronouns",
                titleAr = "مراجعة جدول خطط جينا وضمائر التملك",
                pageNumber = 108,
                summaryAr = "مراجعة going to مع أيام الأسبوع وضمائر التملك (mine, his, hers).",
                keySentences = listOf(
                    "What is Jena going to do on Monday? She's going to buy a present for Mum." to "ماذا ستفعل جينا يوم الإثنين؟ ستشتري هدية لأمها.",
                    "On Thursday, she's going to practise English." to "يوم الخميس ستتدرب على اللغة الإنجليزية.",
                    "The scarf is hers. The cap is his. The book is mine." to "الوشاح لها، القبعة له، والكتاب لي."
                )
            ),
            BookLesson(
                unitNumber = 8,
                lessonNumber = 4,
                titleEn = "Let's sing! Our goodbye party",
                titleAr = "أنشودة حفلة الوداع لنهاية الصف الخامس",
                pageNumber = 109,
                summaryAr = "أنشودة الاحتفال بنهاية الصف الخامس الابتدائي!",
                keySentences = listOf(
                    "There's going to be a party! It's the end of Year 5." to "ستكون هناك حفلة! إنها نهاية الصف الخامس.",
                    "There are going to be games, races and a quiz!" to "ستكون هناك ألعاب وسباقات ومسابقة!"
                ),
                readingTitle = "Song: Our goodbye party (Page 109)",
                readingTextEn = "There's going to be a party! It's the end of Year 5. We're going to have a party for you, and you and me. We're going to have lots of fun, a picnic in the park, cake and jelly, strawberries and big red cherries! There are going to be games, races, and a quiz about countries and places!"
            )
        ),
        fillBlanks = listOf(
            FillBlankItem("u8_fb1", 8, "What is Jena going to do on Friday? She ___ going to visit Grandma.", "مراجعة going to (صفحة 108)", listOf("is", "are", "was", "am"), "is", "مع She نستخدم is going to."),
            FillBlankItem("u8_fb2", 8, "The pharmacy is ___ the bank and the toy shop.", "مراجعة حروف المكان (صفحة 107)", listOf("between", "behind", "opposite", "on"), "between", "بين مكانين نستخدم between."),
            FillBlankItem("u8_fb3", 8, "Where ___ Firas at 2 o'clock? He was at the shoe shop.", "مراجعة الماضي البسيط (صفحة 107)", listOf("was", "were", "is", "are"), "was", "فراس مفرد في الماضي فنستخدم was.")
        ),
        sentenceOrders = listOf(
            SentenceOrderItem("u8_so1", 8, "She's going to buy a present for Mum.", "هي ستشتري هدية للأم."),
            SentenceOrderItem("u8_so2", 8, "The bakery is next to the shoe shop.", "المخبز بجانب محل الأحذية."),
            SentenceOrderItem("u8_so3", 8, "There's going to be a party!", "ستكون هناك حفلة!")
        ),
        dialogues = listOf(
            DialogueExercise("u8_d1", 8, "Jena", "Where was Firas at 2 o'clock?", "أين كان فراس الساعة الثانية؟", listOf("He was at the shoe shop.", "He is Iraqi.", "Yes, there is.", "Once a week."), "He was at the shoe shop.", "من ساعة صفحة 107 كان فراس في محل الأحذية الساعة 2.")
        ),
        readingQuestions = listOf(
            ReadingQuestion("u8_rq1", 8, "Jena's Planner (p.108)", "What is Jena going to do on Friday?", "ماذا ستفعل جينا يوم الجمعة؟", listOf("Visit Grandma", "Watch a film", "Go shopping", "Practise English"), "Visit Grandma", "في جدول جينا يوم الجمعة: visit Grandma."),
            ReadingQuestion("u8_rq2", 8, "Our goodbye party (p.109)", "What is the party for?", "لماذا تقام الحفلة في الأنشودة؟", listOf("The end of Year 5", "Winter holiday", "A museum trip", "A football match"), "The end of Year 5", "تُقام الحفلة بمناسبة نهاية الصف الخامس (end of Year 5).")
        ),
        listeningTracks = listOf(
            ListeningTrack(
                trackNumber = 100,
                unitNumber = 8,
                titleEn = "Track 100: Song — Our goodbye party",
                titleAr = "المقطع 100: أنشودة حفلة نهاية الصف الخامس",
                scriptEn = "There's going to be a party! It's the end of Year 5. We're going to have a picnic and play in the park. We're going to have cake and jelly, strawberries and big, red cherries! There are going to be games, races, and a quiz about countries and places! Now, let's get ready and sing this song!",
                summaryAr = "الأنشودة الختامية لكتاب الصف الخامس الابتدائي.",
                questionEn = "What fruits are mentioned in the goodbye party song?",
                options = listOf("Strawberries and cherries", "Apples and bananas", "Oranges and pears", "Grapes and dates"),
                correctAnswer = "Strawberries and cherries"
            )
        )
    )

    val all: List<BookUnit> = listOf(unit5, unit6, unit7, unit8)
}
