package com.omnitool.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * OmniTool Icon System
 * 
 * Rules from UI/UX spec:
 * - Outline icons only
 * - Consistent stroke width
 * - Rounded terminals
 * - Minimal geometry
 * - No mixed icon styles
 * - Icons must be recognizable in < 1 second
 */
object OmniToolIcons {
    
    // Navigation Icons
    val Home: ImageVector = Icons.Outlined.Home
    val Favorites: ImageVector = Icons.Outlined.FavoriteBorder
    val Vault: ImageVector = Icons.Outlined.Lock
    val Settings: ImageVector = Icons.Outlined.Settings
    val Back: ImageVector = Icons.Outlined.ArrowBack
    val Search: ImageVector = Icons.Outlined.Search
    val Camera: ImageVector = Icons.Outlined.CameraAlt
    val Refresh: ImageVector = Icons.Outlined.Refresh
    val Check: ImageVector = Icons.Outlined.CheckCircle
    
    // Text & Developer Tools
    val JsonFormatter: ImageVector = Icons.Outlined.DataObject
    val XmlFormatter: ImageVector = Icons.Outlined.Code
    val Markdown: ImageVector = Icons.Outlined.Description
    val Regex: ImageVector = Icons.Outlined.FindReplace
    val Base64: ImageVector = Icons.Outlined.Transform
    val UrlEncode: ImageVector = Icons.Outlined.Link
    val UrlEncoder: ImageVector = Icons.Outlined.Link
    val Hash: ImageVector = Icons.Outlined.Tag
    val TextDiff: ImageVector = Icons.Outlined.Compare
    val CaseConverter: ImageVector = Icons.Outlined.TextFormat
    val DuplicateRemover: ImageVector = Icons.Outlined.RemoveCircleOutline
    val Whitespace: ImageVector = Icons.Outlined.SpaceBar
    val WordCount: ImageVector = Icons.Outlined.Numbers
    val LoremIpsum: ImageVector = Icons.Outlined.Notes
    val RandomString: ImageVector = Icons.Outlined.Shuffle
    val Clipboard: ImageVector = Icons.Outlined.ContentPaste
    val Scratchpad: ImageVector = Icons.Outlined.EditNote
    val HtmlPreview: ImageVector = Icons.Outlined.Language
    val CsvViewer: ImageVector = Icons.Outlined.TableChart
    val Csv: ImageVector = Icons.Outlined.TableChart
    
    // File & Media Tools
    val ImageCompress: ImageVector = Icons.Outlined.Compress
    val ImageResize: ImageVector = Icons.Outlined.AspectRatio
    val ImageConvert: ImageVector = Icons.Outlined.SwapHoriz
    val ImageToPdf: ImageVector = Icons.Outlined.PictureAsPdf
    val PdfSplit: ImageVector = Icons.Outlined.CallSplit
    val PdfMerge: ImageVector = Icons.Outlined.CallMerge
    val PdfExtract: ImageVector = Icons.Outlined.PhotoLibrary
    val PdfView: ImageVector = Icons.Outlined.PictureAsPdf
    val QrScan: ImageVector = Icons.Outlined.QrCodeScanner
    val QrScanner: ImageVector = Icons.Outlined.QrCodeScanner
    val QrGenerate: ImageVector = Icons.Outlined.QrCode
    val AudioCut: ImageVector = Icons.Outlined.ContentCut
    val VideoToGif: ImageVector = Icons.Outlined.Gif
    val Exif: ImageVector = Icons.Outlined.Info
    val ExifViewer: ImageVector = Icons.Outlined.Info
    val BatchRename: ImageVector = Icons.Outlined.DriveFileRenameOutline
    val Rename: ImageVector = Icons.Outlined.DriveFileRenameOutline
    
    // Converter Tools
    val UnitConverter: ImageVector = Icons.Outlined.Straighten
    val Currency: ImageVector = Icons.Outlined.CurrencyExchange
    val Timezone: ImageVector = Icons.Outlined.Schedule
    val DateCalc: ImageVector = Icons.Outlined.CalendarMonth
    val AgeCalc: ImageVector = Icons.Outlined.Cake
    val BaseConverter: ImageVector = Icons.Outlined.ChangeCircle
    val StorageConverter: ImageVector = Icons.Outlined.Storage
    val SpeedConverter: ImageVector = Icons.Outlined.Speed
    val FuelCalc: ImageVector = Icons.Outlined.LocalGasStation
    val PercentageCalc: ImageVector = Icons.Outlined.Percent
    val Percentage: ImageVector = Icons.Outlined.Percent
    val Temperature: ImageVector = Icons.Outlined.DeviceThermostat
    val Bmi: ImageVector = Icons.Outlined.MonitorWeight
    val BmiCalculator: ImageVector = Icons.Outlined.MonitorWeight
    val LoanCalculator: ImageVector = Icons.Outlined.Calculate
    val NumberBase: ImageVector = Icons.Outlined.ChangeCircle
    
    // Security Tools
    val PasswordGen: ImageVector = Icons.Outlined.Password
    val PasswordStrength: ImageVector = Icons.Outlined.Security
    val EncryptedNotes: ImageVector = Icons.Outlined.LockOpen
    val FileEncrypt: ImageVector = Icons.Outlined.EnhancedEncryption
    val OtpGenerator: ImageVector = Icons.Outlined.Pin
    val RandomNumber: ImageVector = Icons.Outlined.Casino
    val SecureDelete: ImageVector = Icons.Outlined.DeleteForever
    
    // Utility Tools
    val Stopwatch: ImageVector = Icons.Outlined.Timer
    val Timer: ImageVector = Icons.Outlined.Alarm
    val Countdown: ImageVector = Icons.Outlined.HourglassEmpty
    val RandomPicker: ImageVector = Icons.Outlined.Shuffle
    val DiceRoller: ImageVector = Icons.Outlined.Casino
    val ColorPicker: ImageVector = Icons.Outlined.Palette
    val Flashlight: ImageVector = Icons.Outlined.FlashlightOn
    val SoundMeter: ImageVector = Icons.Outlined.GraphicEq
    val Calculator: ImageVector = Icons.Outlined.Calculate
    val TipCalculator: ImageVector = Icons.Outlined.Receipt
    val QuickNotes: ImageVector = Icons.Outlined.StickyNote2
    val Notes: ImageVector = Icons.Outlined.StickyNote2
    val Compass: ImageVector = Icons.Outlined.Explore
    val WorldClock: ImageVector = Icons.Outlined.Public
    val Speed: ImageVector = Icons.Outlined.Speed
    val Speedometer: ImageVector = Icons.Outlined.Speed
    val Level: ImageVector = Icons.Outlined.Architecture
    val Dice: ImageVector = Icons.Outlined.Casino
    val Shuffle: ImageVector = Icons.Outlined.Shuffle
    val FlashlightOn: ImageVector = Icons.Outlined.FlashlightOn
    val FlashlightOff: ImageVector = Icons.Outlined.FlashlightOff
    
    // Action Icons
    val Copy: ImageVector = Icons.Outlined.ContentCopy
    val Paste: ImageVector = Icons.Outlined.ContentPaste
    val Share: ImageVector = Icons.Outlined.Share
    val Save: ImageVector = Icons.Outlined.Save
    val Export: ImageVector = Icons.Outlined.FileDownload
    val Import: ImageVector = Icons.Outlined.FileUpload
    val Clear: ImageVector = Icons.Outlined.Clear
    val Undo: ImageVector = Icons.Outlined.Undo
    val Redo: ImageVector = Icons.Outlined.Redo
    val Check: ImageVector = Icons.Outlined.Check
    val Close: ImageVector = Icons.Outlined.Close
    val MoreVert: ImageVector = Icons.Outlined.MoreVert
    val MoreHoriz: ImageVector = Icons.Outlined.MoreHoriz
    val Add: ImageVector = Icons.Outlined.Add
    val Remove: ImageVector = Icons.Outlined.Remove
    val ChevronRight: ImageVector = Icons.Outlined.ChevronRight
    val ExpandMore: ImageVector = Icons.Outlined.ExpandMore
    val ExpandLess: ImageVector = Icons.Outlined.ExpandLess
    val Pin: ImageVector = Icons.Outlined.PushPin
    val PinOutline: ImageVector = Icons.Outlined.PushPin
    val Delete: ImageVector = Icons.Outlined.Delete
    val Swap: ImageVector = Icons.Outlined.SwapVert
    val Html: ImageVector = Icons.Outlined.Language
    val Location: ImageVector = Icons.Outlined.LocationOn
    
    // Status Icons
    val Success: ImageVector = Icons.Outlined.CheckCircle
    val Error: ImageVector = Icons.Outlined.Error
    val Warning: ImageVector = Icons.Outlined.Warning
    val Info: ImageVector = Icons.Outlined.Info
    
    // Category Icons
    val TextTools: ImageVector = Icons.Outlined.TextFields
    val FileTools: ImageVector = Icons.Outlined.Folder
    val ConverterTools: ImageVector = Icons.Outlined.SwapHoriz
    val SecurityTools: ImageVector = Icons.Outlined.Shield
    val UtilityTools: ImageVector = Icons.Outlined.Build
}
