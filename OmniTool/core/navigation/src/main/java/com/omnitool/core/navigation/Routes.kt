package com.omnitool.core.navigation

/**
 * Navigation routes for the app
 */
sealed class Route(val route: String) {
    // Main tabs
    data object Home : Route("home")
    data object Favorites : Route("favorites")
    data object Vault : Route("vault")
    data object Settings : Route("settings")
    
    // Text tools
    data object JsonFormatter : Route("tool/json_formatter")
    data object XmlFormatter : Route("tool/xml_formatter")
    data object MarkdownPreview : Route("tool/markdown_preview")
    data object RegexTester : Route("tool/regex_tester")
    data object Base64 : Route("tool/base64")
    data object UrlEncoder : Route("tool/url_encoder")
    data object HashGenerator : Route("tool/hash_generator")
    data object TextDiff : Route("tool/text_diff")
    data object CaseConverter : Route("tool/case_converter")
    data object DuplicateRemover : Route("tool/duplicate_remover")
    data object WhitespaceCleaner : Route("tool/whitespace_cleaner")
    data object WordCounter : Route("tool/word_counter")
    data object LoremIpsum : Route("tool/lorem_ipsum")
    data object RandomString : Route("tool/random_string")
    data object ClipboardHistory : Route("tool/clipboard_history")
    data object Scratchpad : Route("tool/code_scratchpad")
    data object HtmlPreview : Route("tool/html_preview")
    data object CsvViewer : Route("tool/csv_viewer")
    
    // File tools
    data object ImageCompress : Route("tool/image_compressor")
    data object ImageResize : Route("tool/image_resize")
    data object ImageConverter : Route("tool/image_converter")
    data object ImageToPdf : Route("tool/image_to_pdf")
    data object PdfSplit : Route("tool/pdf_split")
    data object PdfMerge : Route("tool/pdf_merge")
    data object PdfExtract : Route("tool/pdf_extract")
    data object PdfViewer : Route("tool/pdf_viewer")
    data object QrScanner : Route("tool/qr_scanner")
    data object QrGenerator : Route("tool/qr_generator")
    data object AudioCutter : Route("tool/audio_cutter")
    data object VideoToGif : Route("tool/video_to_gif")
    data object ExifViewer : Route("tool/exif_viewer")
    data object BatchRename : Route("tool/batch_rename")
    
    // Converter tools
    data object UnitConverter : Route("tool/unit_converter")
    data object CurrencyConverter : Route("tool/currency_converter")
    data object TimezoneConverter : Route("tool/timezone_converter")
    data object DateCalculator : Route("tool/date_calculator")
    data object AgeCalculator : Route("tool/age_calculator")
    data object BaseConverter : Route("tool/base_converter")
    data object StorageConverter : Route("tool/storage_converter")
    data object ColorConverter : Route("tool/color_converter")
    data object TemperatureConverter : Route("tool/temperature_converter")
    data object SpeedConverter : Route("tool/speed_converter")
    data object FuelCalculator : Route("tool/fuel_calculator")
    data object Percentage : Route("tool/percentage_calculator")
    
    // Security tools
    data object PasswordGenerator : Route("tool/password_generator")
    data object PasswordStrength : Route("tool/password_strength")
    data object EncryptedNotes : Route("tool/encrypted_notes")
    data object FileEncryption : Route("tool/file_encryption")
    data object OtpGenerator : Route("tool/otp_generator")
    data object RandomNumber : Route("tool/random_number")
    data object SecureDelete : Route("tool/secure_delete")
    
    // Utility tools
    data object Stopwatch : Route("tool/stopwatch")
    data object Timer : Route("tool/timer")
    data object CountdownTimer : Route("tool/countdown_timer")
    data object RandomPicker : Route("tool/random_picker")
    data object DiceRoller : Route("tool/dice_roller")
    data object ColorPicker : Route("tool/color_picker")
    data object Flashlight : Route("tool/flashlight")
    data object SoundMeter : Route("tool/sound_meter")
    data object Calculator : Route("tool/calculator")
    data object TipCalculator : Route("tool/tip_calculator")
    data object QuickNotes : Route("tool/quick_notes")
    
    // Additional tools from NavHost
    data object WorldClock : Route("tool/world_clock")
    data object TextCase : Route("tool/text_case")
    data object Compass : Route("tool/compass")
    data object LoanCalculator : Route("tool/loan_calculator")
    data object NumberBase : Route("tool/number_base")
    data object Temperature : Route("tool/temperature")
    data object BmiCalculator : Route("tool/bmi_calculator")
    data object Speedometer : Route("tool/speedometer")
    data object Level : Route("tool/level")
}
