package com.example.data.makharij

enum class MakhrajRegion(val titleAr: String, val subtitleAr: String, val iconName: String) {
    JAWF("الجَوف", "التجويف الصوتي الممتد عبر الحلق والفم", "air"),
    HALQ("الحَلْق", "منطقة الحنجرة وأوتار الصوت ولسان المزمار", "mic"),
    LISAN("اللِّسَان", "المخرج الأعظم ويخرج منه 18 حرفاً", "record_voice_over"),
    SHAFATAN("الشَّفَتَان", "من باطن الشفة أو بانطباقهما وانفتاحهما", "sentiment_satisfied"),
    KHAYSHUM("الخَيْشُوم", "التجويف الأنفي ومخرج صوت الغُنَّة", "auto_awesome")
}

data class ArabicLetterMakhraj(
    val letter: String,
    val name: String,
    val region: MakhrajRegion,
    val subRegion: String,
    val phoneticCharacteristics: List<String>, // e.g. ["مفخم", "قلقلة", "شديد"]
    val articulationDescription: String,
    val vocalizationTip: String,
    val sampleWords: List<String>,
    val testPhrases: List<String> = listOf("أَ$letter", "أُ$letter", "إِ$letter", "أْ$letter")
)

object MakharijRepository {
    val allLetters: List<ArabicLetterMakhraj> = listOf(
        // --- 1. الجوف ---
        ArabicLetterMakhraj(
            letter = "ا",
            name = "الألف المادية",
            region = MakhrajRegion.JAWF,
            subRegion = "الجوف ومجرى الهواء الصوتي",
            phoneticCharacteristics = listOf("مرقق دائماً", "صوت ممتد", "حرف مد ولين"),
            articulationDescription = "تخرج من التجويف الفموي الحلقي بتباعد الفكين دون أي احتكاك في الفم.",
            vocalizationTip = "احرص على فتح الفم رأسياً دون إمالة الصوت أو كتمه.",
            sampleWords = listOf("قَالَ", "سَمَاء", "نَهَار")
        ),
        ArabicLetterMakhraj(
            letter = "و",
            name = "الواو المادية",
            region = MakhrajRegion.JAWF,
            subRegion = "الجوف مع ضم الشفتين",
            phoneticCharacteristics = listOf("مرقق", "مجهور", "حرف مد"),
            articulationDescription = "تخرج بامتداد الصوت من الجوف مع انضمام الشفتين وترك فرجة دائرية.",
            vocalizationTip = "أحكم ضم الشفتين مع رفع أقصى اللسان دون تضييق المجرى.",
            sampleWords = listOf("يَقُولُ", "قُلُوب", "نُور")
        ),
        ArabicLetterMakhraj(
            letter = "ي",
            name = "الياء المادية",
            region = MakhrajRegion.JAWF,
            subRegion = "الجوف مع خفض الفك",
            phoneticCharacteristics = listOf("مرقق", "مستفل", "حرف مد"),
            articulationDescription = "تخرج بامتداد الصوت من الجوف مع انخفاض الفك السفلي وارتفاع وسط اللسان.",
            vocalizationTip = "اخفض الفك السفلي قليلاً لتحقيق صفاء صوت الكسرة الممدودة.",
            sampleWords = listOf("قِيلَ", "سَبِيل", "بَصِير")
        ),

        // --- 2. الحلق ---
        ArabicLetterMakhraj(
            letter = "ء",
            name = "الهمزة",
            region = MakhrajRegion.HALQ,
            subRegion = "أقصى الحلق",
            phoneticCharacteristics = listOf("شديد", "مجهور", "مرقق دائماً"),
            articulationDescription = "تخرج من أوتار الحنجرة عند انطباقهما ثم انفتاحهما فجأة.",
            vocalizationTip = "انطقها بحزم ونقاء دون تفخيم عند مجاورة الحروف المفخمة.",
            sampleWords = listOf("أَمْر", "مُؤْمِن", "بَرِيء")
        ),
        ArabicLetterMakhraj(
            letter = "هـ",
            name = "الهاء",
            region = MakhrajRegion.HALQ,
            subRegion = "أقصى الحلق",
            phoneticCharacteristics = listOf("مهموس", "رخو", "خفي"),
            articulationDescription = "تخرج من الحنجرة عند جريان النفس الخفيف بدون تضييق شديد.",
            vocalizationTip = "أعطها نفساً كافياً حتى لا تختفي خاصة في نهاية الكلمة.",
            sampleWords = listOf("هُدَى", "شَهْر", "وَجْه")
        ),
        ArabicLetterMakhraj(
            letter = "ع",
            name = "العين",
            region = MakhrajRegion.HALQ,
            subRegion = "وسط الحلق",
            phoneticCharacteristics = listOf("متوسط (بيني)", "مجهور", "مرقق"),
            articulationDescription = "تخرج برجوع لسان المزمار إلى الخلف نحو الجدار الخلفي للبلعوم.",
            vocalizationTip = "اضغط برفق في منتصف الحلق لتسمع الصوت الرخيم المميز للعين العربية.",
            sampleWords = listOf("عَلِيم", "يَعْمَل", "مَعْرُوف")
        ),
        ArabicLetterMakhraj(
            letter = "ح",
            name = "الحاء",
            region = MakhrajRegion.HALQ,
            subRegion = "وسط الحلق",
            phoneticCharacteristics = listOf("مهموس", "رخو", "صافٍ"),
            articulationDescription = "تخرج من وسط الحلق مع تضييق الفراغ وجريان هواء صافٍ ناعم.",
            vocalizationTip = "دع الهواء يجري بنعومة دون خشونة أو خلطها بصوت الخاء.",
            sampleWords = listOf("حَكِيم", "رَحْمَة", "فَرَح")
        ),
        ArabicLetterMakhraj(
            letter = "غ",
            name = "الغين",
            region = MakhrajRegion.HALQ,
            subRegion = "أدنى الحلق",
            phoneticCharacteristics = listOf("مفخم", "مستعلٍ", "رخو مجهور"),
            articulationDescription = "تخرج من منطقة اتصال أصل اللسان بالحلق بالقرب من اللهاة.",
            vocalizationTip = "فخّم صوتها بحذر واجعل الصوت يجري فيها بسلاسة وفخامة.",
            sampleWords = listOf("غَفُور", "مَغْفِرَة", "بَالِغ")
        ),
        ArabicLetterMakhraj(
            letter = "خ",
            name = "الخاء",
            region = MakhrajRegion.HALQ,
            subRegion = "أدنى الحلق",
            phoneticCharacteristics = listOf("مفخم", "مهموس", "رخو ذو حفيف"),
            articulationDescription = "تخرج من أدنى الحلق مع احتكاك الهواء بأصل اللسان وسقف الحلق الرخو.",
            vocalizationTip = "لا تبالغ في خشونة الصوت، بل اجعل الحفيف لطيفاً ومتزناً.",
            sampleWords = listOf("خَالِق", "يَخْرُج", "شَيْخ")
        ),

        // --- 3. اللسان ---
        ArabicLetterMakhraj(
            letter = "ق",
            name = "القاف",
            region = MakhrajRegion.LISAN,
            subRegion = "أقصى اللسان مع الحنك اللحمي",
            phoneticCharacteristics = listOf("مفخم بقوة", "شديد", "قلقلة عند السكون"),
            articulationDescription = "تخرج بالتصاق أقصى اللسان بالمنطقة الرخوة اللحمية من سقف الحلق.",
            vocalizationTip = "اضرب المخرج بحزم، واقلقله عند السكون بصوت جهوري مفخم.",
            sampleWords = listOf("قُرْآن", "حَقِيقَة", "يَنْطِق")
        ),
        ArabicLetterMakhraj(
            letter = "ك",
            name = "الكاف",
            region = MakhrajRegion.LISAN,
            subRegion = "أقصى اللسان مع الحنك العظمي",
            phoneticCharacteristics = listOf("شديد", "مهموس", "مرقق"),
            articulationDescription = "تخرج من أقصى اللسان تحت مخرج القاف قليلاً مع الحنك الصلب.",
            vocalizationTip = "انطق الشدة أولاً ثم أتبعها بهمس هواء خفيف جداً عند الانفراج.",
            sampleWords = listOf("كِتَاب", "شُكْر", "مَلَك")
        ),
        ArabicLetterMakhraj(
            letter = "ج",
            name = "الجيم الفصيحة",
            region = MakhrajRegion.LISAN,
            subRegion = "وسط اللسان مع سقف الحنك",
            phoneticCharacteristics = listOf("شديد مجهور", "قلقلة", "غير احتكاكي"),
            articulationDescription = "تخرج بالتصاق محكم لوسط اللسان بقبة الحنك الأعلى وحبس الصوت تماماً.",
            vocalizationTip = "احذر من إخراج هواء معها (د/ش) أو نطقها قاهرية (G)، بل جيم فصيحة محكمة.",
            sampleWords = listOf("جَمِيل", "مَجِيد", "بُرُوج")
        ),
        ArabicLetterMakhraj(
            letter = "ش",
            name = "الشين",
            region = MakhrajRegion.LISAN,
            subRegion = "وسط اللسان",
            phoneticCharacteristics = listOf("رخو", "مهموس", "صفة التفشي"),
            articulationDescription = "تخرج بارتفاع وسط اللسان دون التصاق، مع انتشار الهواء في الفم.",
            vocalizationTip = "أشبع صفة التفشي بانتشار الهواء في تجويف الفم بنعومة.",
            sampleWords = listOf("شَمْس", "مَشْكُور", "عَرْش")
        ),
        ArabicLetterMakhraj(
            letter = "ض",
            name = "الضاد (حرف العروبة)",
            region = MakhrajRegion.LISAN,
            subRegion = "إحدى حافتي اللسان أو كلاهما",
            phoneticCharacteristics = listOf("مفخم مطبق", "مستطيل", "رخو مجهور"),
            articulationDescription = "تخرج بالتصاق إحدى حافتي اللسان أو كلتيهما بالأضراس العليا مع استطالة الصوت.",
            vocalizationTip = "لا تنطقها ظاءً أو دالاً مفخمة، بل اجعل الضغط على حافة اللسان والأضراس.",
            sampleWords = listOf("ضِيَاء", "مَضْمُون", "أَرْض")
        ),
        ArabicLetterMakhraj(
            letter = "ل",
            name = "اللام",
            region = MakhrajRegion.LISAN,
            subRegion = "أدنى حافة اللسان إلى منتهاها",
            phoneticCharacteristics = listOf("متوسط", "انحراف الصوت", "مرقق ويفخم في لفظ الجلالة"),
            articulationDescription = "تخرج بملامسة طرف حافة اللسان للثة الأسنان العليا مع انحراف الهواء من الجانبين.",
            vocalizationTip = "اجعل اللام رقيقة إلا عند لفظ الجلالة المسبوق بفتح أو ضم.",
            sampleWords = listOf("لِسَان", "عِلْم", "جَلِيل")
        ),
        ArabicLetterMakhraj(
            letter = "ن",
            name = "النون",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع لثة الثنايا العليا",
            phoneticCharacteristics = listOf("متوسط", "مجهور", "مصاحب لغنة الخيشوم"),
            articulationDescription = "يقرع طرف اللسان لثة الثنايا العليا، ويكتمل صوتها بغنة من الخيشوم.",
            vocalizationTip = "وازن بين صوت المخرج الفموي والغنة الخيشومية الرقيقة.",
            sampleWords = listOf("نُور", "مَنْزِل", "أَمَان")
        ),
        ArabicLetterMakhraj(
            letter = "ر",
            name = "الراء",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مائلاً لظهره",
            phoneticCharacteristics = listOf("متوسط", "تكرير خفيف", "تفخيم وترقيق بحسب الحركة"),
            articulationDescription = "تخرج بضرب طرف اللسان لما يحاذيه من لثة الثنايا العليا بارتعادة واحدة خفيفة.",
            vocalizationTip = "احذر من كثرة الارتعاد، واضبط تفخيمها إذا فتحت أو ضمت وترقيقها إذا كسرت.",
            sampleWords = listOf("رَحِيم", "قُرْبَان", "كَرِيم")
        ),
        ArabicLetterMakhraj(
            letter = "ط",
            name = "الطاء",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع أصول الثنايا العليا",
            phoneticCharacteristics = listOf("أقوى الحروف", "مفخم مطبق", "شديد وقلقلة"),
            articulationDescription = "تخرج بالتصاق طرف اللسان بأصول الثنايا العليا مع انطباق طائفة من اللسان على الحنك.",
            vocalizationTip = "أقوى حروف الهجاء استعلاءً وإطباقاً، انطقها بملء الفم وقوة.",
            sampleWords = listOf("طَيِّب", "مَطْلُوب", "مُحِيط")
        ),
        ArabicLetterMakhraj(
            letter = "د",
            name = "الدال",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع أصول الثنايا العليا",
            phoneticCharacteristics = listOf("شديد", "مجهور", "قلقلة مرققة"),
            articulationDescription = "تخرج بانطباق طرف اللسان على أصول الثنايا العليا بانحباس تام للنفس والصوت.",
            vocalizationTip = "انطقها مرققة تماماً واحذر أن تشبه التاء أو تفخم بجوار المفخمات.",
            sampleWords = listOf("دُعَاء", "مَدِينَة", "بَلَد")
        ),
        ArabicLetterMakhraj(
            letter = "ت",
            name = "التاء",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع أصول الثنايا العليا",
            phoneticCharacteristics = listOf("شديد", "مهموس", "مرقق"),
            articulationDescription = "تخرج من طرف اللسان مع أصول الثنايا العليا مع همس هوائي خفيف عند الفتح.",
            vocalizationTip = "اضبط الهمس ليكون لطيفاً وطبيعياً دون إفراط.",
            sampleWords = listOf("تَوْبَة", "مَفْتُوح", "بَيْت")
        ),
        ArabicLetterMakhraj(
            letter = "ص",
            name = "الصاد",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان وفوق الثنايا السفلى",
            phoneticCharacteristics = listOf("مفخم مطبق", "صفير", "رخو مهموس"),
            articulationDescription = "تخرج من طرف اللسان مع صفحتي الثنايا السفلى مع صوت صفير قوي يخرج بين الأسنان.",
            vocalizationTip = "أخرج صفيرها المفخم مع استعلاء أقصى اللسان وإطباق وسطه.",
            sampleWords = listOf("صَبْر", "مَصِير", "خَالِص")
        ),
        ArabicLetterMakhraj(
            letter = "ز",
            name = "الزاي",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان وفوق الثنايا السفلى",
            phoneticCharacteristics = listOf("مجهور", "صفير ناعم", "رخو مرقق"),
            articulationDescription = "تخرج من طرف اللسان مع ما بين الثنايا العليا والسفلى مع اهتزاز مجهور للأوتار.",
            vocalizationTip = "صفير صافٍ ومجهور يشبه أزيز النحل برقة.",
            sampleWords = listOf("زَهْرَة", "مِيزَان", "فَوْز")
        ),
        ArabicLetterMakhraj(
            letter = "س",
            name = "السين",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان وفوق الثنايا السفلى",
            phoneticCharacteristics = listOf("مهموس", "صفير دقيق", "رخو مرقق"),
            articulationDescription = "تخرج من طرف اللسان مع ما فوق الثنايا السفلى بجريان النفس والصوت.",
            vocalizationTip = "اجعل صوت السين حاداً ونقياً دون خلطه بالصاد.",
            sampleWords = listOf("سَلَام", "مَسْجِد", "شَمْس")
        ),
        ArabicLetterMakhraj(
            letter = "ظ",
            name = "الظاء",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع أطراف الثنايا العليا",
            phoneticCharacteristics = listOf("مفخم مطبق", "لثوي", "رخو مجهور"),
            articulationDescription = "تخرج بخروج يسير لطرف اللسان لملامسة حواف الأسنان الأمامية العليا.",
            vocalizationTip = "أخرج جزءاً صغيراً من طرف اللسان مع تفخيم الحرف وإطباقه.",
            sampleWords = listOf("ظِلّ", "مَظْلُوم", "حَفِيظ")
        ),
        ArabicLetterMakhraj(
            letter = "ذ",
            name = "الذال",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع أطراف الثنايا العليا",
            phoneticCharacteristics = listOf("مرقق", "لثوي", "رخو مجهور"),
            articulationDescription = "تخرج بملامسة طرف اللسان لحواف الثنايا العليا دون ضغط شديد.",
            vocalizationTip = "احذر من تحويلها إلى زاي، بل أخرج رأس اللسان بلطف.",
            sampleWords = listOf("ذِكْر", "مَذْكُور", "لَذِيذ")
        ),
        ArabicLetterMakhraj(
            letter = "ث",
            name = "الثاء",
            region = MakhrajRegion.LISAN,
            subRegion = "طرف اللسان مع أطراف الثنايا العليا",
            phoneticCharacteristics = listOf("مرقق", "لثوي مهموس", "رخو خفيف"),
            articulationDescription = "تخرج من رأس طرف اللسان مع أطراف الثنايا العليا مع جريان النفس.",
            vocalizationTip = "احرص على خروج الهواء الرقيق اللطيف مع طرف اللسان.",
            sampleWords = listOf("ثَوَاب", "مَثَل", "حَدِيث")
        ),

        // --- 4. الشفتان ---
        ArabicLetterMakhraj(
            letter = "ف",
            name = "الفاء",
            region = MakhrajRegion.SHAFATAN,
            subRegion = "بطن الشفة السفلى مع أطراف الثنايا العليا",
            phoneticCharacteristics = listOf("مهموس", "رخو", "مرقق"),
            articulationDescription = "تخرج من بطن الشفة السفلى مع ملامسة أطراف الأسنان العليا وجريان الهواء.",
            vocalizationTip = "اجعل التلامس رقيقاً وجريان الهواء حراً وواضحاً.",
            sampleWords = listOf("فَجْر", "مَفْهُوم", "لَطِيف")
        ),
        ArabicLetterMakhraj(
            letter = "ب",
            name = "الباء",
            region = MakhrajRegion.SHAFATAN,
            subRegion = "انطباق الشفتين بقوة",
            phoneticCharacteristics = listOf("شديد مجهور", "قلقلة", "مرقق"),
            articulationDescription = "تخرج بانطباق الشفتين على بعضهما انطباقاً تاماً مع حبس الصوت والنفس ثم انفراجهما.",
            vocalizationTip = "اقلقلها عند السكون بوضوح، وانطقها بنقاء وقوة صوتية.",
            sampleWords = listOf("بَرَكَة", "مُبَارَك", "حَبِيب")
        ),
        ArabicLetterMakhraj(
            letter = "م",
            name = "الميم",
            region = MakhrajRegion.SHAFATAN,
            subRegion = "انطباق الشفتين مع غنة",
            phoneticCharacteristics = listOf("متوسط", "مجهور", "نصفها فموي ونصفها خيشومي"),
            articulationDescription = "تخرج بانطباق الشفتين برفق مع إخراج صوت رنين الغنة من الأنف.",
            vocalizationTip = "انطباق الشفتين لطيف ومريح، والغنة تكمل رنين الحرف العذب.",
            sampleWords = listOf("مُعَلِّم", "شَمْس", "رَحِيم")
        ),

        // --- 5. الخيشوم ---
        ArabicLetterMakhraj(
            letter = "غُنَّة",
            name = "صوت الغنة الخيشومي",
            region = MakhrajRegion.KHAYSHUM,
            subRegion = "أعلى الأنف (الخيشوم)",
            phoneticCharacteristics = listOf("صوت رخيم", "مركب في النون والميم", "طبيعي ومشدد"),
            articulationDescription = "صوت أغن لذيذ يخرج من التجويف الأنفي لا عمل للسان فيه.",
            vocalizationTip = "أشبع زمن الغنة بمقدار حركتين عند التشديد أو الإخفاء والإدغام.",
            sampleWords = listOf("إِنَّ", "ثُمَّ", "مَن يَقُول")
        )
    )

    fun getLettersByRegion(region: MakhrajRegion): List<ArabicLetterMakhraj> {
        return allLetters.filter { it.region == region }
    }
}
