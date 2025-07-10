// script.js

const LANGUAGES = [
    { name: "English", code: "en" },
    { name: "Spanish", code: "es" },
    { name: "French", code: "fr" },
    // Add more languages as needed
];

document.addEventListener("DOMContentLoaded", () => {
    const sourceLangDropdown = document.getElementById("source-lang");
    const targetLangDropdown = document.getElementById("target-lang");
    
    // Populate dropdowns
    LANGUAGES.forEach(lang => {
        const option = document.createElement("option");
        option.value = lang.code;
        option.textContent = lang.name;

        sourceLangDropdown.appendChild(option);
        targetLangDropdown.appendChild(option.cloneNode(true));
    });

    // Swap languages
    document.getElementById("swap-languages").addEventListener("click", () => {
        const temp = sourceLangDropdown.value;
        sourceLangDropdown.value = targetLangDropdown.value;
        targetLangDropdown.value = temp;
    });

    // Translate text
    document.getElementById("translate-button").addEventListener("click", async () => {
        const sourceLang = sourceLangDropdown.value;
        const targetLang = targetLangDropdown.value;
        const text = document.getElementById("input-text").value;

        if (!text) {
            alert("Please enter text to translate.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:12345/translate`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ sourceLang, targetLang, text }),
            });

            if (!response.ok) throw new Error("Translation failed");

            const translatedText = await response.text();
            document.getElementById("output-text").value = translatedText;

        } catch (error) {
            console.error(error);
            alert("Error connecting to server.");
        }
    });
});
