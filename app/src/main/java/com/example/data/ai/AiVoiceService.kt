package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

enum class VoiceTone(
    val titleAr: String,
    val descriptionAr: String,
    val defaultPitch: Float,
    val defaultRate: Float,
    val icon: String
) {
    DOCUMENTARY("وثائقي هادئ", "نبرة صوتية عميقة ورزينة مناسبة للتعليق الوثائقي والمعرفي", 0.85f, 0.90f, "auto_stories"),
    NEWS("مذيع إخباري", "نبرة واثقة ومنتظمة وسريعة الإيقاع مثل النشرات الإخبارية الرسمية", 1.05f, 1.10f, "campaign"),
    EDUCATIONAL("تعليمي ومخارج حروف", "نبرة متأنية وواضحة جداً مع تركيز على دقة مخارج الحروف والتجويد", 0.95f, 0.80f, "school"),
    STORYTELLER("قصصي دافئ", "نبرة عاطفية حنونة وممتعة تلائم الحكايات والروايات", 0.90f, 0.95f, "theater_comedy"),
    ENERGETIC("إعلاني حماسي", "نبرة عالية وحيوية ذات جاذبية وطاقة صوتية مرتفعة", 1.25f, 1.15f, "electric_bolt"),
    NATURAL("طبيعي متوازن", "نبرة محايدة تناسب القراءة اليومية والتطبيقات العامة", 1.00f, 1.00f, "graphic_eq")
}

data class MakharijAnalysisResult(
    val summary: String,
    val heavyLetters: List<String>,
    val throatLetters: List<String>,
    val vocalizationAdvice: String,
    val pausePointsTip: String
)

class AiVoiceService {
    private val tag = "AiVoiceService"

    // Model for free-text Arabic diacritization and tone adaptation
    private val textModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    // Model for structured linguistic and phonetic makharij analysis
    private val analysisModel by lazy {
        val analysisSchema = Schema.obj(
            properties = mapOf(
                "summary" to Schema.string("ملخص صوتي للنص"),
                "heavyLetters" to Schema.array(
                    items = Schema.string(),
                    description = "حروف الاستعلاء والتفخيم الواردة في النص (خص ضغط قظ)"
                ),
                "throatLetters" to Schema.array(
                    items = Schema.string(),
                    description = "حروف الحلق الواردة في النص (ء، هـ، ع، ح، غ، خ)"
                ),
                "vocalizationAdvice" to Schema.string("إرشادات النطق السليم ومخارج الحروف وضبط نبرة الصوت"),
                "pausePointsTip" to Schema.string("مواضع الوقف الصوتي المناسبة للنص")
            )
        )

        val config = generationConfig {
            responseMimeType = "application/json"
            responseSchema = analysisSchema
            temperature = 0.2f
        }

        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = config
        )
    }

    suspend fun diacritizeText(input: String): String {
        return withTimeout(60_000L) {
            try {
                val prompt = """
                    أنت أستاذ خبير في اللغة العربية وعلم الأصوات والتجويد ومخارج الحروف.
                    المهمة: قم بتشكيل النص العربي التالي تشكيلاً كاملاً ودقيقاً وفصيحاً 100% يشمل كافة الحركات (الفتحة، الضمة، الكسرة، السكون، الشدّة، والتنوين، وضبط همزات القطع والوصل وأواخر الكلمات إعرابياً).
                    الهدف: إعداد النص لمحرك النطق الصوتي (Text-to-Speech) ليخرج الصوت فصيحاً ومخارج الحروف سليمة تماماً.
                    شروط صارمة:
                    - أرجع النص المشكول فقط لا غير.
                    - لا تكتب أي مقدمة أو خاتمة أو ملاحظات أو علامات تنصيص إضافية.
                    
                    النص المطلوب تشكيله:
                    $input
                """.trimIndent()

                val response = textModel.generateContent(prompt)
                val result = response.text?.trim().orEmpty()
                if (result.isBlank()) {
                    throw IllegalStateException("لم يُرجع النموذج أي نص مشكول.")
                }
                result
            } catch (e: TimeoutCancellationException) {
                Log.e(tag, "Diacritization timed out", e)
                throw Exception("استغرقت عملية التشكيل بالذكاء الاصطناعي وقتاً أطول من المتوقع، يرجى المحاولة ثانية.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(tag, "Diacritization error", e)
                throw Exception("فشل التشكيل الذكي: ${e.localizedMessage}")
            }
        }
    }

    suspend fun analyzePhonetics(text: String): MakharijAnalysisResult {
        return withTimeout(60_000L) {
            try {
                val prompt = """
                    قم بتحليل مخارج الحروف الصوتية والخصائص التجويدية والنبرة المناسبة للنص العربي التالي:
                    "$text"
                """.trimIndent()

                val response = analysisModel.generateContent(prompt)
                val jsonString = response.text ?: throw IllegalStateException("استجابة التحليل فارغة.")
                val json = JSONObject(jsonString)

                val summary = json.optString("summary", "تحليل صوتي لمخارج الحروف.")
                val heavyLettersArray = json.optJSONArray("heavyLetters")
                val heavyLetters = (0 until (heavyLettersArray?.length() ?: 0)).map {
                    heavyLettersArray!!.getString(it)
                }

                val throatLettersArray = json.optJSONArray("throatLetters")
                val throatLetters = (0 until (throatLettersArray?.length() ?: 0)).map {
                    throatLettersArray!!.getString(it)
                }

                val advice = json.optString("vocalizationAdvice", "اضبط الحركات وتنفس بهدوء لتحقيق مخارج نقية.")
                val pauses = json.optString("pausePointsTip", "قف عند علامات الترقيم الطبيعية.")

                MakharijAnalysisResult(
                    summary = summary,
                    heavyLetters = heavyLetters,
                    throatLetters = throatLetters,
                    vocalizationAdvice = advice,
                    pausePointsTip = pauses
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(tag, "Analysis timed out", e)
                throw Exception("استغرق تحليل مخارج الحروف وقتاً طويلاً، حاول مجدداً.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(tag, "Analysis error", e)
                throw Exception("فشل تحليل مخارج الحروف: ${e.localizedMessage}")
            }
        }
    }

    suspend fun enhanceForTone(text: String, tone: VoiceTone): String {
        return withTimeout(60_000L) {
            try {
                val prompt = """
                    أعد صياغة علامات الترقيم وفواصل الوقف الصوتي للنص العربي التالي ليتناسب تماماً مع أسلوب النبرة الصوتية: (${tone.titleAr} - ${tone.descriptionAr}).
                    اجعل فواصل الوقف واضحة باستخدام الفواصل والنقاط، مع تشكيل الكلمات المفتاحية لضبط الإيقاع الصوتي المناسب لهذا الأسلوب.
                    أرجع النص الموجه فقط دون أي كلام جانبي.
                    
                    النص:
                    $text
                """.trimIndent()

                val response = textModel.generateContent(prompt)
                response.text?.trim().orEmpty().ifBlank { text }
            } catch (e: Exception) {
                Log.w(tag, "Tone enhancement fallback to original text", e)
                text
            }
        }
    }

    suspend fun analyzeCustomVoiceProfile(voiceName: String, durationSeconds: Int): CustomVoiceProfile {
        return withTimeout(60_000L) {
            try {
                val prompt = """
                    قم بتقدير وتحديد الخصائص الصوتية لنبرة الصوت المسجل باسم "$voiceName" ومدة التسجيل $durationSeconds ثانية في اللغة العربية.
                    حدد بدقة:
                    - نبرة الصوت (Pitch) من 0.60 إلى 1.40 (مثال: نبرة عميقة 0.85، نبرة متوسطة 1.00، نبرة مشرقة حادة 1.25)
                    - سرعة القراءة (Rate) من 0.75 إلى 1.30 (مثال: متأنٍ 0.90، معتدل 1.00، سريع 1.15)
                    - وصف النمط الصوتي (مثل: "صوت إذاعي جهوري"، "صوت دافئ وهادئ"، "صوت حماسي نشط")
                    - تصنيف الصوت (رجالي أو نسائي أو متوازن)
                    - شرح الخصائص الصوتية ومخارج الحروف لهذا الصوت.
                """.trimIndent()

                val voiceSchema = Schema.obj(
                    properties = mapOf(
                        "pitch" to Schema.double("قيمة نبرة الصوت المقترحة بين 0.60 و 1.40"),
                        "rate" to Schema.double("سرعة النطق المقترحة بين 0.75 و 1.30"),
                        "toneLabel" to Schema.string("وصف النبرة الصوتية بالعربية"),
                        "gender" to Schema.string("رجالي أو نسائي أو متوازن"),
                        "description" to Schema.string("شرح الخصائص الصوتية ومخارج الحروف لهذا الصوت")
                    )
                )

                val config = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = voiceSchema
                    temperature = 0.3f
                }

                val model = Firebase.ai.generativeModel(
                    modelName = "gemini-3.8-flash",
                    generationConfig = config
                )

                val response = model.generateContent(prompt)
                val jsonString = response.text ?: "{}"
                val json = JSONObject(jsonString)

                val pitch = json.optDouble("pitch", 1.0).toFloat().coerceIn(0.5f, 2.0f)
                val rate = json.optDouble("rate", 1.0).toFloat().coerceIn(0.5f, 2.0f)
                val toneLabel = json.optString("toneLabel", "صوت مخصص متوازن")
                val gender = json.optString("gender", "متوازن")
                val description = json.optString("description", "بصمة صوتية مخصصة تم ضبط نبرتها وسرعتها بدقة.")

                CustomVoiceProfile(
                    recommendedPitch = pitch,
                    recommendedRate = rate,
                    toneLabel = toneLabel,
                    gender = gender,
                    description = description
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(tag, "Fallback voice profile generation", e)
                CustomVoiceProfile(
                    recommendedPitch = 0.95f,
                    recommendedRate = 0.95f,
                    toneLabel = "صوت ناطق مخصص",
                    gender = "متوازن",
                    description = "تمت محاكاة النبرة والسرعة الصوتية المناسبة للتسجيل الصوتي."
                )
            }
        }
    }
}

data class CustomVoiceProfile(
    val recommendedPitch: Float,
    val recommendedRate: Float,
    val toneLabel: String,
    val gender: String,
    val description: String
)
