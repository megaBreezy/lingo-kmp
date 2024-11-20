# LingoKMP 🗺️✨
**Effortless Localization for Kotlin Multiplatform Projects**

Lingo makes adding translations to your Kotlin Multiplatform (KMP) apps a breeze. With the power of the Lingo Translate Plugin and the Lingo KMP library, you can generate robust, type-safe translation classes from simple JSON files, ensuring seamless localization across Android, iOS, and beyond. 🌍

---

## 🚀 Features

- **Type-Safe Translations**: Never worry about typos in translation keys again!
- **Auto-Detection of Language**: Automatically adapts to the user's system language, with flexible fallbacks.
- **Seamless Integration**: A Gradle plugin to scan your translation files and generate the classes you need.
- **Multi-Platform Support**: Designed for Kotlin Multiplatform, with Android and iOS implementations baked in.

---

## 📦 Installation

### 1. Add the Lingo Translate Plugin
In your `build.gradle.kts` (root or module-specific):

```kotlin
plugins {
    id("com.github.megabreezy.lingo.translate") version "0.0.1"
}
```

---

### 2. Add the LingoKMP Library

```kotlin
dependencies {
    implementation("com.github.megabreezy.lingo:lingo-kmp:0.0.1")
}
```

---

## 📚 Usage

### 1. Add Your Translation Files
Place JSON files in `src/commonMain/resources/translations`. Each file should represent a language:

* `en.json`:
```json
{
  "greeting": "Hello",
  "farewell": "Goodbye"
}
```
* `es.json`:
```json
{
  "greeting": "Hola",
  "farewell": "Adiós"
}
```

### 2. Initialize Translations
Call the `Translations_Initializer.initialize()` function during your app's startup. For example, in a shared `init` block or platform-specific initialization code:

```kotlin
fun initializeApp()
{
    Translations_Initializer.initialize() // Ensures translations are registered for use
}
```

### 3. Generate Translation Classes
Build your project. The plugin will generate a type-safe `Translations` class you can use in your code:

```kotlin
Lingo.get_string(Translation_Key.Greeting) // Outputs "Hello" or "Hola" based on the user's language

```

### 4. Language Fallbacks
Lingo automatically detects the user's language. If a regional variant (e.g., `en-GB`) isn't available, it falls back to the base language (e.g., `en`).

---

## 🤝 Contributions
We welcome contributions! Found a bug? Want to suggest a feature? [Open an issue](https://github.com/megaBreezy/lingo-kmp/issues) or create a pull request.

## 📜 License
This project is licensed under the [MIT License](https://chatgpt.com/g/g-dkd5K1v2U-code-companion/c/LICENSE).