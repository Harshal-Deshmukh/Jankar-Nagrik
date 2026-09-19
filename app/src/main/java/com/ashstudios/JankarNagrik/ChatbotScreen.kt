package com.ashstudios.JankarNagrik

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen() {
    val isHindi = LocalIsHindi.current
    val coroutineScope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val initialGreeting = if (isHindi) {
        "नमस्ते! मैं आपका एनएसएफडीसी योजना सहायक हूँ। ऋण पात्रता, आवश्यक दस्तावेज़, ब्याज दर या आवेदन केंद्र के बारे में कुछ भी पूछें।"
    } else {
        "Hello! I am your NSFDC Scheme Assistant. Ask me anything about loan eligibility, required documents, moratorium periods, interest rates, or channel partners."
    }

    var messages by remember(isHindi) {
        mutableStateOf(listOf("Bot: $initialGreeting"))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                val isBot = message.startsWith("Bot:")
                val displayMsg = if (isBot) message.removePrefix("Bot: ") else message.removePrefix("You: ")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
                ) {
                    Surface(
                        color = if (isBot) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = displayMsg,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isBot) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "सोच रहा हूँ..." else "Thinking...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(if (isHindi) "पात्रता, दस्तावेज़ या योजना पूछें..." else "Ask about eligibility, documents, schemes...")
                },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (query.isNotBlank() && !isLoading) {
                        val userText = query.trim()
                        messages = messages + "You: $userText"
                        query = ""
                        isLoading = true

                        coroutineScope.launch {
                            try {
                                if (BuildConfig.GEMINI_API_KEY.isBlank()) {
                                    throw IllegalStateException("GEMINI_API_KEY is empty. Please check local.properties and rebuild.")
                                }
                                val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
                                    modelName = "gemini-3.6-flash",
                                    apiKey = BuildConfig.GEMINI_API_KEY
                                )
                                val systemPrompt = "You are an AI assistant for marginalized SC entrepreneurs under MoSJE/NSFDC. Answer user queries about loan eligibility, required documents, moratorium periods, interest rates, and channel partners in simple words, responding in " + (if (isHindi) "Hindi (हिन्दी)" else "English") + " based on the user's selected language."
                                val prompt = "$systemPrompt\n\nUser Question: $userText"
                                val response = generativeModel.generateContent(prompt)
                                val botText = response.text?.trim() ?: if (isHindi) "माफ़ कीजिए, कोई उत्तर नहीं मिल सका।" else "I could not generate a response."
                                messages = messages + "Bot: $botText"
                            } catch (e: Exception) {
                                android.util.Log.e("ChatbotScreen", "Gemini call failed: ${e.message}", e)
                                val fallback = if (isHindi) {
                                    "मैं अभी ऑफ़लाइन मोड में हूँ। आधिकारिक एनएसएफडीसी योजना दिशानिर्देशों के अनुसार, ₹5,00,000 तक वार्षिक आय वाले अनुसूचित जाति के उद्यमी 4%-8% रियायती ब्याज दर पर ऋण के लिए पात्र हैं। अधिक जानकारी के लिए अपने राज्य चैनल एजेंसी (SCA) या बैंक शाखा से संपर्क करें।"
                                } else {
                                    "I am currently operating in offline mode. Under official NSFDC guidelines, SC entrepreneurs with family income up to ₹5,00,000 are eligible for concessional credit (4%-8% interest). Please consult your nearest SCA or bank channel partner for application assistance."
                                }
                                messages = messages + "Bot: $fallback"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = query.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isHindi) "भेजें" else "Send")
            }
        }
    }
}
