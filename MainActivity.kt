package com.example.picklauncher

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.BatteryManager
import android.provider.AlarmClock
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.key
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import kotlin.math.cos
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ───────────── 색상 ─────────────
private val Orange = Color(0xFFF99E1A)
private val OrangeDark = Color(0xFFD97F00)
private val IceBlue = Color(0xFF9BE3FF)
private val SlotBlue = Color(0xFF3FA9F5)
private val NameBlue = Color(0xFF36B6FF)
private val PanelDark = Color(0xCC0F1624)
private val SlotLine = Color(0xFFB9C6DA)
private val BgTop = Color(0xFF34425E)
private val BgMid = Color(0xFF1C2639)
private val BgBottom = Color(0xFF0B111C)

private const val SLOT_COUNT = 5
private const val RECENT_MAX = 8

// ───────────── 데이터 ─────────────
enum class AppGroup(val label: String, val sub: String) {
    RECENT("최근", "RECENT"),
    GAME("게임", "GAME"),
    SOCIAL("소셜", "SOCIAL"),
    MEDIA("미디어", "MEDIA"),
    TOOL("도구", "TOOL"),
    ETC("기타", "ETC"),
    SYSTEM("시스템", "SYSTEM")
}

@Immutable
data class AppEntry(
    val label: String,
    val pkg: String,
    val icon: ImageBitmap,
    val group: AppGroup
)

// 육각형 (꼭짓점이 위로)
private val HexShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w / 2f, 0f)
    lineTo(w, h * 0.25f)
    lineTo(w, h * 0.75f)
    lineTo(w / 2f, h)
    lineTo(0f, h * 0.75f)
    lineTo(0f, h * 0.25f)
    close()
}

class MainActivity : ComponentActivity() {
    private var resumeTick by mutableIntStateOf(0)
    lateinit var widgetHost: AppWidgetHost
    var widgetConfigCallback: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        widgetHost = AppWidgetHost(this, WIDGET_HOST_ID)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                BackHandler { /* 홈 화면이므로 뒤로가기 무시 */ }
                LauncherScreen(resumeTick, this@MainActivity)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            widgetHost.startListening()
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        try {
            widgetHost.stopListening()
        } catch (e: Exception) {
        }
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        resumeTick++ // 앱 설치/삭제 후 돌아오면 목록 갱신
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_WIDGET_CONFIG) {
            widgetConfigCallback?.invoke(resultCode)
        }
    }
}

// ───────────── 앱 목록 / 저장 ─────────────
private fun classify(info: ApplicationInfo): AppGroup {
    val isSystem = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    return when (info.category) {
        ApplicationInfo.CATEGORY_GAME -> AppGroup.GAME
        ApplicationInfo.CATEGORY_SOCIAL,
        ApplicationInfo.CATEGORY_NEWS -> AppGroup.SOCIAL
        ApplicationInfo.CATEGORY_AUDIO,
        ApplicationInfo.CATEGORY_VIDEO,
        ApplicationInfo.CATEGORY_IMAGE -> AppGroup.MEDIA
        ApplicationInfo.CATEGORY_PRODUCTIVITY,
        ApplicationInfo.CATEGORY_MAPS,
        ApplicationInfo.CATEGORY_ACCESSIBILITY -> AppGroup.TOOL
        else -> if (isSystem) AppGroup.SYSTEM else AppGroup.ETC
    }
}

private val iconCache = ConcurrentHashMap<String, ImageBitmap>()

private fun loadApps(ctx: Context): List<AppEntry> {
    val pm = ctx.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .filter { it.activityInfo.packageName != ctx.packageName }
        .distinctBy { it.activityInfo.packageName }
        .map { ri ->
            AppEntry(
                label = ri.loadLabel(pm).toString(),
                pkg = ri.activityInfo.packageName,
                icon = iconCache.getOrPut(ri.activityInfo.packageName) {
                    ri.loadIcon(pm).toBitmap(192, 192).asImageBitmap()
                },
                group = classify(ri.activityInfo.applicationInfo)
            )
        }
        .sortedWith(
            compareBy<AppEntry>({ it.group.ordinal }, { it.label.lowercase() })
        )
}

private fun loadSlots(prefs: SharedPreferences): List<String> {
    val raw = prefs.getString("slots", "") ?: ""
    val parts = raw.split("|")
    return List(SLOT_COUNT) { parts.getOrNull(it) ?: "" }
}

private fun saveSlots(prefs: SharedPreferences, slots: List<String>) {
    prefs.edit().putString("slots", slots.joinToString("|")).apply()
}

private fun loadRecents(prefs: SharedPreferences): List<String> {
    val raw = prefs.getString("recent", "") ?: ""
    return raw.split("|").filter { it.isNotEmpty() }
}

private fun launchApp(ctx: Context, pkg: String) {
    try {
        ctx.packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(it)
        }
    } catch (e: Exception) {
        // 실행 불가 앱은 무시
    }
}

private fun openAppInfo(ctx: Context, pkg: String) {
    try {
        val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$pkg"))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    } catch (e: Exception) {
    }
}

private fun uninstallApp(ctx: Context, pkg: String) {
    try {
        val i = Intent(Intent.ACTION_DELETE, Uri.parse("package:$pkg"))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    } catch (e: Exception) {
    }
}

// ───────────── 배경사진 / 숨김 / 위젯 저장 ─────────────
private const val REQ_WIDGET_CONFIG = 7001
private const val WIDGET_HOST_ID = 1024
private const val WALLPAPER_FILE = "wallpaper.jpg"

private fun loadHidden(prefs: SharedPreferences): Set<String> {
    val raw = prefs.getString("hidden", "") ?: ""
    return raw.split("|").filter { it.isNotEmpty() }.toSet()
}

private fun loadWidgetIds(prefs: SharedPreferences): List<Int> {
    val raw = prefs.getString("widgets", "") ?: ""
    return raw.split(",").mapNotNull { it.toIntOrNull() }
}

private fun loadWallpaper(ctx: Context): Bitmap? {
    return try {
        val f = File(ctx.filesDir, WALLPAPER_FILE)
        if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
    } catch (e: Throwable) {
        null
    }
}

private fun deleteWallpaper(ctx: Context) {
    try {
        File(ctx.filesDir, WALLPAPER_FILE).delete()
    } catch (e: Exception) {
    }
}

// 갤러리에서 고른 사진을 화면에 맞게 줄여서(긴 변 1920px) 앱 내부에 저장
private fun saveWallpaper(ctx: Context, uri: Uri): Bitmap? {
    return try {
        val cr = ctx.contentResolver
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            null
        } else {
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= 1920) sample *= 2
            val opts = BitmapFactory.Options()
            opts.inSampleSize = sample
            val decoded = cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            if (decoded == null) {
                null
            } else {
                var orientation = ExifInterface.ORIENTATION_NORMAL
                try {
                    cr.openInputStream(uri)?.use {
                        orientation = ExifInterface(it).getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                    }
                } catch (e: Exception) {
                }
                val m = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.postScale(1f, -1f)
                }
                val fixed = if (m.isIdentity) {
                    decoded
                } else {
                    Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, m, true)
                }
                File(ctx.filesDir, WALLPAPER_FILE).outputStream().use {
                    fixed.compress(Bitmap.CompressFormat.JPEG, 90, it)
                }
                fixed
            }
        }
    } catch (e: Throwable) {
        null
    }
}

// ───────────── 기본 즐겨찾기 (전화 / 문자 / 카메라 / 브라우저 / 설정) ─────────────
private fun defaultSlots(ctx: Context, apps: List<AppEntry>): List<String> {
    val pm = ctx.packageManager
    val valid = apps.map { it.pkg }.toSet()
    fun pkgFor(i: Intent): String? {
        return try {
            pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName
        } catch (e: Exception) {
            null
        }
    }
    val candidates = listOf(
        pkgFor(Intent(Intent.ACTION_DIAL)),
        pkgFor(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))),
        pkgFor(Intent(MediaStore.ACTION_IMAGE_CAPTURE)),
        pkgFor(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))),
        pkgFor(Intent(Settings.ACTION_SETTINGS))
    )
    val picked = candidates.filterNotNull().filter { it in valid }.distinct()
    return List(SLOT_COUNT) { picked.getOrNull(it) ?: "" }
}

// ───────────── 인텐트 도우미 ─────────────
private fun openIntent(ctx: Context, intent: Intent): Boolean {
    return try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

private fun openCalendar(ctx: Context) {
    val viewTime = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("content://com.android.calendar/time/" + System.currentTimeMillis())
    )
    if (!openIntent(ctx, viewTime)) {
        openIntent(ctx, Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALENDAR))
    }
}

private fun webSearch(ctx: Context, q: String) {
    val url = "https://www.google.com/search?q=" + Uri.encode(q.trim())
    openIntent(ctx, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

// ───────────── 위젯 목록 ─────────────
@Immutable
class WidgetChoice(
    val info: AppWidgetProviderInfo,
    val label: String,
    val appLabel: String,
    val icon: ImageBitmap?,
    val key: String
)

private fun loadWidgetChoices(ctx: Context): List<WidgetChoice> {
    val pm = ctx.packageManager
    val mgr = AppWidgetManager.getInstance(ctx)
    val list = ArrayList<WidgetChoice>()
    for (info in mgr.installedProviders) {
        try {
            val pkg = info.provider.packageName
            val label = info.loadLabel(pm)?.toString() ?: pkg
            val appLabel = try {
                pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
            } catch (e: Exception) {
                pkg
            }
            val icon = try {
                info.loadIcon(ctx, 0)?.toBitmap(96, 96)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
            list.add(WidgetChoice(info, label, appLabel, icon, info.provider.flattenToString()))
        } catch (e: Exception) {
        }
    }
    return list.sortedWith(
        compareBy<WidgetChoice>({ it.appLabel.lowercase() }, { it.label.lowercase() })
    )
}

// ───────────── 검색 (초성 지원) ─────────────
private const val CHOSEONG = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"

private fun choseongOf(s: String): String {
    val sb = StringBuilder()
    for (ch in s) {
        if (ch in '가'..'힣') {
            sb.append(CHOSEONG[(ch - '가') / 588])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

private fun matchesQuery(app: AppEntry, q: String): Boolean {
    val query = q.trim().lowercase()
    if (query.isEmpty()) return true
    val label = app.label.lowercase()
    if (label.contains(query)) return true
    val onlyChoseong = query.all { CHOSEONG.indexOf(it) >= 0 }
    return onlyChoseong && choseongOf(label).contains(query)
}

// ───────────── 메인 화면 ─────────────
@Composable
fun LauncherScreen(resumeTick: Int, activity: MainActivity) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val screenW = LocalConfiguration.current.screenWidthDp
    val tile = if (screenW >= 600) 64.dp else 52.dp
    val prefs = remember { ctx.getSharedPreferences("pick_launcher", Context.MODE_PRIVATE) }

    var allApps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var hidden by remember { mutableStateOf(loadHidden(prefs)) }
    var slots by remember { mutableStateOf(loadSlots(prefs)) }
    var recents by remember { mutableStateOf(loadRecents(prefs)) }
    var selectedPkg by remember { mutableStateOf<String?>(prefs.getString("last", null)) }
    var scrollTick by remember { mutableIntStateOf(0) }
    var dir by remember { mutableIntStateOf(0) }
    var effects by remember { mutableStateOf(prefs.getBoolean("effects", true)) }
    var menuApp by remember { mutableStateOf<AppEntry?>(null) }
    var drawerOpen by remember { mutableStateOf(false) }
    var drawerKeyboard by remember { mutableStateOf(true) }
    var pickSlot by remember { mutableIntStateOf(-1) }
    var settingsOpen by remember { mutableStateOf(false) }
    var widgetsOpen by remember { mutableStateOf(false) }
    var pickerOpen by remember { mutableStateOf(false) }
    var widgetIds by remember { mutableStateOf(loadWidgetIds(prefs)) }
    var pendingWidget by remember { mutableIntStateOf(-1) }
    var wallpaper by remember { mutableStateOf<ImageBitmap?>(null) }
    var wallDim by remember { mutableStateOf(prefs.getFloat("wallDim", 0.35f)) }
    val launchFx = remember { Animatable(0f) }
    val widgetMgr = remember { AppWidgetManager.getInstance(ctx) }
    val widgetHost = activity.widgetHost

    val apps = remember(allApps, hidden) { allApps.filter { it.pkg !in hidden } }

    // 앱 목록 + (처음 한 번) 기본 즐겨찾기 채우기
    LaunchedEffect(resumeTick) {
        val loaded = withContext(Dispatchers.Default) { loadApps(ctx) }
        if (loaded.map { it.pkg } != allApps.map { it.pkg }) allApps = loaded
        if (loaded.isNotEmpty() && !prefs.getBoolean("slots_init", false)) {
            prefs.edit().putBoolean("slots_init", true).apply()
            if (slots.all { it.isEmpty() }) {
                val d = withContext(Dispatchers.Default) { defaultSlots(ctx, loaded) }
                slots = d
                saveSlots(prefs, d)
            }
        }
    }

    // 저장된 배경사진 불러오기
    LaunchedEffect(Unit) {
        val bmp = withContext(Dispatchers.IO) { loadWallpaper(ctx) }
        if (bmp != null) wallpaper = bmp.asImageBitmap()
    }

    val pickWallpaper = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val bmp = withContext(Dispatchers.IO) { saveWallpaper(ctx, uri) }
                if (bmp != null) {
                    wallpaper = bmp.asImageBitmap()
                    Toast.makeText(ctx, "배경을 바꿨어요", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(ctx, "사진을 불러오지 못했어요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val selected = apps.firstOrNull { it.pkg == selectedPkg } ?: apps.firstOrNull()
    val selectedIndex = apps.indexOfFirst { it.pkg == selected?.pkg }
    val rosterGroups = remember(apps, recents) {
        val byPkg = apps.associateBy { it.pkg }
        val recentApps = recents.mapNotNull { byPkg[it] }
        val base = apps.groupBy { it.group }.toList()
        if (recentApps.isEmpty()) base else listOf(AppGroup.RECENT to recentApps) + base
    }
    val rosterState = rememberLazyListState()

    // 가벼운 진동 + 클릭음
    fun tick() {
        if (effects) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    // 미리보기 (누르는 동안 중앙에 표시)
    fun select(pkg: String) {
        if (selectedPkg != pkg) {
            selectedPkg = pkg
            dir = 0
            tick()
        }
    }

    fun step(delta: Int) {
        if (apps.isEmpty()) return
        val i = if (selectedIndex < 0) 0 else (selectedIndex + delta + apps.size) % apps.size
        select(apps[i].pkg)
        dir = delta
        scrollTick++
    }

    fun updateSlots(new: List<String>) {
        slots = new
        saveSlots(prefs, new)
    }

    fun togglePin(app: AppEntry) {
        if (app.pkg in slots) {
            updateSlots(slots.map { if (it == app.pkg) "" else it })
        } else {
            val empty = slots.indexOfFirst { it.isEmpty() }
            val target = if (empty >= 0) empty else SLOT_COUNT - 1
            updateSlots(slots.toMutableList().also { it[target] = app.pkg })
        }
    }

    fun pinTo(app: AppEntry, index: Int) {
        val m = slots.map { if (it == app.pkg) "" else it }.toMutableList()
        m[index] = app.pkg
        updateSlots(m)
    }

    fun hideApp(app: AppEntry) {
        val nh = hidden + app.pkg
        hidden = nh
        prefs.edit().putString("hidden", nh.joinToString("|")).apply()
        if (app.pkg in slots) updateSlots(slots.map { if (it == app.pkg) "" else it })
        if (selectedPkg == app.pkg) selectedPkg = null
        Toast.makeText(ctx, "숨겼어요 (설정에서 다시 표시할 수 있어요)", Toast.LENGTH_SHORT).show()
    }

    fun showHiddenApps() {
        hidden = emptySet()
        prefs.edit().remove("hidden").apply()
        Toast.makeText(ctx, "숨긴 앱을 모두 표시해요", Toast.LENGTH_SHORT).show()
    }

    fun startApp(pkg: String) {
        val newRecents = (listOf(pkg) + recents.filter { it != pkg }).take(RECENT_MAX)
        recents = newRecents
        selectedPkg = pkg
        prefs.edit()
            .putString("recent", newRecents.joinToString("|"))
            .putString("last", pkg)
            .apply()
        if (effects) {
            scope.launch {
                launchFx.snapTo(0f)
                launchFx.animateTo(1f, tween(140))
                launchApp(ctx, pkg)
                launchFx.animateTo(0f, tween(380))
            }
        } else {
            launchApp(ctx, pkg)
        }
    }

    fun openMenu(app: AppEntry) {
        if (effects) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        menuApp = app
    }

    fun openDrawer(keyboard: Boolean, slot: Int) {
        drawerKeyboard = keyboard
        pickSlot = slot
        drawerOpen = true
    }

    fun closeDrawer() {
        drawerOpen = false
        pickSlot = -1
    }

    fun toggleEffects() {
        effects = !effects
        prefs.edit().putBoolean("effects", effects).apply()
        Toast.makeText(
            ctx,
            if (effects) "효과 켜짐" else "효과 꺼짐 (배터리 절약)",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ── 위젯 ──
    fun commitWidget(id: Int) {
        val n = widgetIds + id
        widgetIds = n
        prefs.edit().putString("widgets", n.joinToString(",")).apply()
    }

    fun dropWidgetId(id: Int) {
        try {
            widgetHost.deleteAppWidgetId(id)
        } catch (e: Exception) {
        }
    }

    fun finishBind(id: Int) {
        val info = try {
            widgetMgr.getAppWidgetInfo(id)
        } catch (e: Exception) {
            null
        }
        if (info != null && info.configure != null) {
            activity.widgetConfigCallback = { rc ->
                activity.widgetConfigCallback = null
                if (rc == Activity.RESULT_OK) commitWidget(id) else dropWidgetId(id)
            }
            try {
                widgetHost.startAppWidgetConfigureActivityForResult(activity, id, 0, REQ_WIDGET_CONFIG, null)
            } catch (e: Exception) {
                activity.widgetConfigCallback = null
                commitWidget(id)
            }
        } else {
            commitWidget(id)
        }
    }

    val bindLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val id = pendingWidget
        pendingWidget = -1
        if (id >= 0) {
            if (result.resultCode == Activity.RESULT_OK) finishBind(id) else dropWidgetId(id)
        }
    }

    fun addWidget(choice: WidgetChoice) {
        pickerOpen = false
        val id = try {
            widgetHost.allocateAppWidgetId()
        } catch (e: Exception) {
            -1
        }
        if (id < 0) return
        val ok = try {
            widgetMgr.bindAppWidgetIdIfAllowed(id, choice.info.provider)
        } catch (e: Exception) {
            false
        }
        if (ok) {
            finishBind(id)
        } else {
            pendingWidget = id
            try {
                val i = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, choice.info.provider)
                bindLauncher.launch(i)
            } catch (e: Exception) {
                pendingWidget = -1
                dropWidgetId(id)
            }
        }
    }

    fun removeWidget(id: Int) {
        dropWidgetId(id)
        val n = widgetIds - id
        widgetIds = n
        prefs.edit().putString("widgets", n.joinToString(",")).apply()
    }

    BackHandler(enabled = menuApp != null || drawerOpen || settingsOpen || widgetsOpen || pickerOpen) {
        when {
            menuApp != null -> menuApp = null
            pickerOpen -> pickerOpen = false
            settingsOpen -> settingsOpen = false
            widgetsOpen -> widgetsOpen = false
            else -> closeDrawer()
        }
    }

    // 스와이프/단계 이동 시 선택한 앱이 있는 그룹으로 로스터 스크롤
    LaunchedEffect(scrollTick) {
        if (scrollTick == 0) return@LaunchedEffect
        val pkg = selected?.pkg ?: return@LaunchedEffect
        val gi = rosterGroups.indexOfFirst { g -> g.second.any { it.pkg == pkg } }
        if (gi >= 0) {
            val col = rosterGroups[gi].second.indexOfFirst { it.pkg == pkg } / 2
            val off = with(density) { (tile + 5.dp).roundToPx() * col }
            rosterState.animateScrollToItem(gi, off)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom)))
    ) {
        // 사용자 배경사진
        val wp = wallpaper
        if (wp != null) {
            Image(
                bitmap = wp,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = wallDim))
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x66000000), Color.Transparent, Color(0x80000000))
                        )
                    )
            )
        }

        BackgroundDecor(effects)

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                // 위로 밀면 전체 앱, 아래로 밀면 검색
                .pointerInput(drawerOpen) {
                    var total = 0f
                    detectVerticalDragGestures(
                        onDragStart = { total = 0f },
                        onDragEnd = {
                            if (!drawerOpen) {
                                if (total < -160f) {
                                    openDrawer(false, -1)
                                } else if (total > 220f) {
                                    openDrawer(true, -1)
                                }
                            }
                        },
                        onDragCancel = { total = 0f },
                        onVerticalDrag = { _, amount -> total += amount }
                    )
                }
        ) {
            Box(Modifier.fillMaxWidth().introSlide(0, (-48).dp)) {
                Header(
                    selected = selected,
                    total = apps.size,
                    index = selectedIndex,
                    onOpenSettings = { settingsOpen = true },
                    onClock = { openIntent(ctx, Intent(AlarmClock.ACTION_SHOW_ALARMS)) },
                    onDate = { openCalendar(ctx) }
                )
            }

            Hero(
                selected = selected,
                dir = dir,
                effects = effects,
                punch = launchFx.value,
                onStep = { d -> step(d) },
                onLaunch = { selected?.let { startApp(it.pkg) } },
                onMenu = { selected?.let { openMenu(it) } },
                modifier = Modifier.weight(1f)
            )

            TeamRow(
                slots = slots,
                apps = apps,
                selectedPkg = selected?.pkg,
                effects = effects,
                onPreview = { select(it) },
                onTap = { index, pkg ->
                    if (pkg.isEmpty()) openDrawer(false, index) else startApp(pkg)
                },
                onLong = { index, pkg ->
                    val app = apps.firstOrNull { it.pkg == pkg }
                    if (app != null) openMenu(app) else openDrawer(false, index)
                }
            )

            Spacer(Modifier.height(12.dp))

            Box(Modifier.fillMaxWidth().introSlide(250, 90.dp)) {
                Roster(
                    groups = rosterGroups,
                    selectedPkg = selected?.pkg,
                    slots = slots,
                    state = rosterState,
                    tile = tile,
                    effects = effects,
                    onPreview = { select(it) },
                    onLaunch = { startApp(it) },
                    onMenu = { openMenu(it) }
                )
            }

            Spacer(Modifier.height(12.dp))

            // 하단 버튼 줄: 위젯 / 앱 검색 / 전체 앱 / 설정
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .introSlide(400, 90.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SideButton(onClick = { widgetsOpen = true }) {
                    ToolIcon(0, 20.dp, Color.White)
                }
                SearchButton(effects, Modifier.weight(1f)) { openDrawer(true, -1) }
                SideButton(onClick = { openDrawer(false, -1) }) {
                    ToolIcon(1, 20.dp, Color.White)
                }
                SideButton(onClick = { settingsOpen = true }) {
                    ToolIcon(2, 22.dp, Color.White)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "탭: 실행  ·  길게: 메뉴  ·  위로: 전체 앱  ·  아래로: 검색",
                color = SlotLine.copy(alpha = 0.8f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp)
            )
        }

        // 전체 앱 / 검색 서랍
        AnimatedVisibility(
            visible = drawerOpen,
            enter = slideInVertically(tween(240)) { it } + fadeIn(tween(240)),
            exit = slideOutVertically(tween(180)) { it } + fadeOut(tween(180))
        ) {
            AppDrawer(
                apps = apps,
                title = if (pickSlot >= 0) "슬롯 ${pickSlot + 1}에 고정할 앱" else "전체 앱 ${apps.size}개",
                autoFocus = drawerKeyboard,
                onTap = { app ->
                    if (pickSlot >= 0) {
                        pinTo(app, pickSlot)
                        closeDrawer()
                    } else {
                        closeDrawer()
                        startApp(app.pkg)
                    }
                },
                onLong = { app -> openMenu(app) },
                onClose = { closeDrawer() }
            )
        }

        // 위젯 보드
        AnimatedVisibility(
            visible = widgetsOpen,
            enter = slideInVertically(tween(240)) { it } + fadeIn(tween(240)),
            exit = slideOutVertically(tween(180)) { it } + fadeOut(tween(180))
        ) {
            WidgetBoard(
                host = widgetHost,
                ids = widgetIds,
                onAdd = { pickerOpen = true },
                onRemove = { removeWidget(it) },
                onClose = { widgetsOpen = false }
            )
        }

        // 위젯 고르기
        AnimatedVisibility(
            visible = pickerOpen,
            enter = slideInVertically(tween(240)) { it } + fadeIn(tween(240)),
            exit = slideOutVertically(tween(180)) { it } + fadeOut(tween(180))
        ) {
            WidgetPicker(
                onPick = { addWidget(it) },
                onClose = { pickerOpen = false }
            )
        }

        // 설정
        AnimatedVisibility(
            visible = settingsOpen,
            enter = slideInVertically(tween(240)) { it } + fadeIn(tween(240)),
            exit = slideOutVertically(tween(180)) { it } + fadeOut(tween(180))
        ) {
            SettingsPanel(
                hasWallpaper = wallpaper != null,
                dim = wallDim,
                onDim = { wallDim = it },
                onDimDone = { prefs.edit().putFloat("wallDim", wallDim).apply() },
                onPickWallpaper = { pickWallpaper.launch("image/*") },
                onResetWallpaper = {
                    deleteWallpaper(ctx)
                    wallpaper = null
                    Toast.makeText(ctx, "기본 배경으로 되돌렸어요", Toast.LENGTH_SHORT).show()
                },
                effects = effects,
                onToggleEffects = { toggleEffects() },
                hiddenCount = hidden.size,
                onShowHidden = { showHiddenApps() },
                onHomeSettings = {
                    if (!openIntent(ctx, Intent(Settings.ACTION_HOME_SETTINGS))) {
                        openIntent(ctx, Intent(Settings.ACTION_SETTINGS))
                    }
                },
                onSystemSettings = { openIntent(ctx, Intent(Settings.ACTION_SETTINGS)) },
                onClose = { settingsOpen = false }
            )
        }

        // 길게 누르기 메뉴
        menuApp?.let { app ->
            AppMenu(
                app = app,
                pinned = app.pkg in slots,
                onDismiss = { menuApp = null },
                onLaunch = {
                    menuApp = null
                    closeDrawer()
                    startApp(app.pkg)
                },
                onTogglePin = {
                    togglePin(app)
                    menuApp = null
                },
                onInfo = {
                    openAppInfo(ctx, app.pkg)
                    menuApp = null
                },
                onHide = {
                    hideApp(app)
                    menuApp = null
                },
                onUninstall = {
                    uninstallApp(ctx, app.pkg)
                    menuApp = null
                }
            )
        }

        // 실행 플래시
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = launchFx.value * 0.45f }
                .background(Color.White)
        )
    }
}

// ───────────── 공용 modifier ─────────────
// 탭 / 누름 / 길게 누름을 한 번에 (누름 상태 콜백 포함)
@Composable
private fun Modifier.tapGestures(
    onPress: () -> Unit = {},
    onPressedChange: (Boolean) -> Unit = {},
    onTap: () -> Unit,
    onLongPress: () -> Unit
): Modifier {
    val press by rememberUpdatedState(onPress)
    val pressedChange by rememberUpdatedState(onPressedChange)
    val tap by rememberUpdatedState(onTap)
    val long by rememberUpdatedState(onLongPress)
    return this.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                press()
                pressedChange(true)
                try {
                    tryAwaitRelease()
                } finally {
                    pressedChange(false)
                }
            },
            onTap = { tap() },
            onLongPress = { long() }
        )
    }
}

// 처음 나타날 때 슬라이드 + 페이드 인
@Composable
private fun Modifier.introSlide(delayMs: Int, fromY: Dp): Modifier {
    val p = remember { Animatable(0f) }
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        p.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
    }
    return this.graphicsLayer {
        translationY = (1f - p.value) * with(density) { fromY.toPx() }
        alpha = p.value
    }
}

// ───────────── 배경 장식 (빛줄기 + 떠다니는 입자) ─────────────
@Composable
private fun BackgroundDecor(effects: Boolean) {
    val t: State<Float> = if (effects) {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
            label = "bgT"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Canvas(Modifier.fillMaxSize()) {
        val w = this.size.width
        val h = this.size.height
        val tt = t.value
        val twoPi = (2.0 * PI).toFloat()
        val drift = sin(tt * twoPi) * w * 0.04f

        val beam1 = Path().apply {
            moveTo(w * 0.50f + drift, 0f)
            lineTo(w * 0.80f + drift, 0f)
            lineTo(w * 0.30f + drift, h)
            lineTo(w * 0.00f + drift, h)
            close()
        }
        drawPath(beam1, Brush.verticalGradient(listOf(Color(0x16FFFFFF), Color.Transparent)))

        val beam2 = Path().apply {
            moveTo(w * 0.85f - drift, 0f)
            lineTo(w * 1.00f - drift, 0f)
            lineTo(w * 0.70f - drift, h)
            lineTo(w * 0.55f - drift, h)
            close()
        }
        drawPath(beam2, Brush.verticalGradient(listOf(Color(0x0DFFFFFF), Color.Transparent)))

        // 하단 비네팅
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0x99000000)),
                startY = h * 0.55f,
                endY = h
            )
        )

        // 떠오르는 입자
        if (effects) {
            val n = 16
            for (i in 0 until n) {
                val seed = ((i * 7919) % 97) / 97f
                val seed2 = ((i * 104729) % 89) / 89f
                val phase = (tt + i / n.toFloat()) % 1f
                val x = w * (0.05f + 0.9f * seed) + sin(phase * twoPi + i) * 10.dp.toPx()
                val y = h * (1f - phase)
                val a = sin(phase * (PI.toFloat())).coerceIn(0f, 1f) * 0.5f
                val r = (1.2f + 2.2f * seed2).dp.toPx()
                val c = if (i % 3 == 0) IceBlue else Color(0xFFFFC46B)
                drawCircle(c.copy(alpha = a * 0.35f), radius = r * 2.2f, center = Offset(x, y))
                drawCircle(c.copy(alpha = a), radius = r, center = Offset(x, y))
            }
        }
    }
}

// ───────────── 글자 효과 ─────────────
@Composable
private fun GhostText(
    text: String,
    size: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    align: TextAlign = TextAlign.Start
) {
    Box(modifier) {
        // 뒤쪽 잔상
        Text(
            text = text,
            color = color.copy(alpha = 0.22f),
            fontSize = size,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = align,
            modifier = Modifier.offset(4.dp, 3.dp)
        )
        Text(
            text = text,
            color = color,
            fontSize = size,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = align,
            style = TextStyle(
                shadow = Shadow(color = Color(0x99000000), offset = Offset(3f, 3f), blurRadius = 6f)
            )
        )
    }
}

// ───────────── 아이콘 ─────────────
@Composable
private fun GroupIcon(group: AppGroup, iconSize: Dp, color: Color) {
    Canvas(Modifier.size(iconSize)) {
        val w = this.size.width
        val h = this.size.height
        when (group) {
            AppGroup.RECENT -> {
                drawCircle(color, radius = w * 0.4f, style = Stroke(w * 0.12f))
                drawLine(color, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f, h * 0.25f), w * 0.1f)
                drawLine(color, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.7f, h * 0.58f), w * 0.1f)
            }
            AppGroup.GAME -> {
                val bw = w * 0.2f
                listOf(0.08f, 0.40f, 0.72f).forEach { x ->
                    drawRect(color, Offset(w * x, h * 0.1f), Size(bw, h * 0.8f))
                }
            }
            AppGroup.SOCIAL -> {
                drawRect(color, Offset(w * 0.38f, 0f), Size(w * 0.24f, h))
                drawRect(color, Offset(0f, h * 0.38f), Size(w, h * 0.24f))
            }
            AppGroup.MEDIA -> {
                val p = Path().apply {
                    moveTo(w * 0.2f, h * 0.08f)
                    lineTo(w * 0.92f, h * 0.5f)
                    lineTo(w * 0.2f, h * 0.92f)
                    close()
                }
                drawPath(p, color)
            }
            AppGroup.TOOL -> {
                val p = Path().apply {
                    moveTo(w * 0.1f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.55f)
                    lineTo(w * 0.5f, h * 0.95f)
                    lineTo(w * 0.1f, h * 0.55f)
                    close()
                }
                drawPath(p, color)
            }
            AppGroup.ETC -> {
                drawCircle(color, radius = w * 0.34f, style = Stroke(w * 0.16f))
            }
            AppGroup.SYSTEM -> {
                drawRect(
                    color,
                    Offset(w * 0.12f, h * 0.12f),
                    Size(w * 0.76f, h * 0.76f),
                    style = Stroke(w * 0.16f)
                )
            }
        }
    }
}

@Composable
private fun SearchIcon(iconSize: Dp, color: Color) {
    Canvas(Modifier.size(iconSize)) {
        val w = this.size.width
        val h = this.size.height
        drawCircle(
            color,
            radius = w * 0.28f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = Stroke(w * 0.12f)
        )
        drawLine(color, Offset(w * 0.63f, h * 0.63f), Offset(w * 0.9f, h * 0.9f), w * 0.14f)
    }
}

@Composable
private fun Badge(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Color(0xDD0E1522))
            .border(1.dp, SlotLine, CircleShape),
        contentAlignment = Alignment.Center
    ) { content() }
}

// 앱 아이콘 (실제 아이콘 그대로 표시)
@Composable
private fun AppIconView(
    app: AppEntry,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    iconScale: Float = 1f
) {
    Image(
        bitmap = app.icon,
        contentDescription = app.label,
        modifier = modifier.size(minOf(width, height) * iconScale)
    )
}

// ───────────── 배터리 ─────────────
@Composable
private fun rememberBattery(): State<Pair<Int, Boolean>> {
    val ctx = LocalContext.current
    return produceState(initialValue = Pair(-1, false)) {
        while (true) {
            try {
                val i = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                if (i != null) {
                    val level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                    val status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val pct = if (level >= 0 && scale > 0) level * 100 / scale else -1
                    val chg = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                    value = Pair(pct, chg)
                }
            } catch (e: Exception) {
            }
            delay(30000L)
        }
    }
}

@Composable
private fun BatteryChip(percent: Int, charging: Boolean) {
    if (percent < 0) return
    val col = when {
        charging -> Color(0xFF6EE79A)
        percent <= 15 -> Color(0xFFFF6B6B)
        else -> SlotLine
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(width = 18.dp, height = 10.dp)) {
            val w = this.size.width
            val h = this.size.height
            val nub = w * 0.1f
            drawRoundRect(
                col,
                Offset(0f, 0f),
                Size(w - nub, h),
                CornerRadius(2.dp.toPx()),
                style = Stroke(1.2.dp.toPx())
            )
            val inset = 2.dp.toPx()
            val fillW = (w - nub - inset * 2f) * (percent.coerceIn(0, 100) / 100f)
            drawRect(col, Offset(inset, inset), Size(fillW.coerceAtLeast(0f), h - inset * 2f))
            drawRect(col, Offset(w - nub, h * 0.3f), Size(nub, h * 0.4f))
        }
        Spacer(Modifier.width(5.dp))
        Text(
            if (charging) "$percent% 충전 중" else "$percent%",
            color = col,
            fontSize = 11.sp
        )
    }
}

// ───────────── 시계 (콜론 깜빡임) ─────────────
@Composable
private fun ClockBlock(onClock: () -> Unit, onDate: () -> Unit) {
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(500L)
        }
    }
    val battery by rememberBattery()
    val date = Date(now)
    val h = SimpleDateFormat("H", Locale.getDefault()).format(date)
    val m = SimpleDateFormat("mm", Locale.getDefault()).format(date)
    val d = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA).format(date)
    val colonOn = (now / 500L) % 2L == 0L

    Row(Modifier.height(IntrinsicSize.Min)) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(Orange)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                "현재 시각",
                color = Orange,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                Modifier.clickable { onClock() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(h, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(
                    ":",
                    color = Color.White.copy(alpha = if (colonOn) 1f else 0.2f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(m, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Text(
                d,
                color = SlotLine,
                fontSize = 11.sp,
                maxLines = 1,
                modifier = Modifier.clickable { onDate() }
            )
            BatteryChip(battery.first, battery.second)
        }
    }
}

// ───────────── 헤더 ─────────────
@Composable
private fun Header(
    selected: AppEntry?,
    total: Int,
    index: Int,
    onOpenSettings: () -> Unit,
    onClock: () -> Unit,
    onDate: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xCC070B14), Color.Transparent)))
            .drawBehind {
                drawLine(
                    Color(0x55FFFFFF),
                    Offset(0f, this.size.height),
                    Offset(this.size.width, this.size.height),
                    1.dp.toPx()
                )
            }
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            // 좌측: 타이틀(길게 누르면 설정) + 시계
            Column(Modifier.weight(1f)) {
                GhostText(
                    "런처",
                    36.sp,
                    modifier = Modifier.tapGestures(onTap = {}, onLongPress = onOpenSettings)
                )
                Text(
                    "앱을 선택하십시오",
                    color = IceBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
                Spacer(Modifier.height(10.dp))
                ClockBlock(onClock, onDate)
            }

            // 우측: 선택한 앱 이름 + 분류
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "+",
                        color = Orange,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.width(4.dp))
                    AnimatedContent(
                        targetState = selected?.label ?: "-",
                        transitionSpec = {
                            (slideInHorizontally(tween(240)) { it / 2 } + fadeIn(tween(240))) togetherWith
                                (slideOutHorizontally(tween(160)) { -it / 2 } + fadeOut(tween(120)))
                        },
                        label = "name"
                    ) { name ->
                        val nameSize = when {
                            name.length <= 8 -> 32.sp
                            name.length <= 14 -> 24.sp
                            else -> 18.sp
                        }
                        GhostText(name, nameSize, align = TextAlign.End)
                    }
                }
                if (selected != null) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selected.group.label,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .size(24.dp, 26.dp)
                                .clip(HexShape)
                                .border(1.5.dp, Orange, HexShape),
                            contentAlignment = Alignment.Center
                        ) {
                            GroupIcon(selected.group, 11.dp, Orange)
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${index + 1} / $total",
                        color = SlotLine,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ───────────── 중앙 영웅(앱) 영역 ─────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Hero(
    selected: AppEntry?,
    dir: Int,
    effects: Boolean,
    punch: Float,
    onStep: (Int) -> Unit,
    onLaunch: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepState by rememberUpdatedState(onStep)

    // 선택이 바뀔 때 번쩍이는 플래시
    val flash = remember { Animatable(0f) }
    LaunchedEffect(selected?.pkg) {
        if (effects) {
            flash.snapTo(0.7f)
            flash.animateTo(0f, tween(520))
        }
    }
    // 숨쉬는 글로우
    val breathe: State<Float>? = if (effects) {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0.55f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(1800, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "breathe"
        )
    } else {
        null
    }

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                var total = 0f
                detectHorizontalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = {
                        if (total < -70f) stepState(1) else if (total > 70f) stepState(-1)
                    },
                    onDragCancel = { total = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        total += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val iconSize = minOf(maxWidth * 0.5f, maxHeight * 0.58f, 220.dp)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                // 발광 (숨쉬듯 깜빡)
                Box(
                    Modifier
                        .size(iconSize * 1.5f)
                        .graphicsLayer { alpha = breathe?.value ?: 0.8f }
                        .background(
                            Brush.radialGradient(listOf(Color(0x66F99E1A), Color.Transparent))
                        )
                )
                // 육각 링
                Box(
                    Modifier
                        .size(width = iconSize * 1.2f, height = iconSize * 1.38f)
                        .clip(HexShape)
                        .border(1.dp, Color(0x55B9C6DA), HexShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(width = iconSize * 1.08f, height = iconSize * 1.24f)
                            .clip(HexShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x33FFFFFF), Color(0x0DFFFFFF))
                                )
                            )
                            .border(2.dp, SlotBlue.copy(alpha = 0.85f), HexShape)
                    )
                }
                // 선택 순간 플래시
                Box(
                    Modifier
                        .size(iconSize * 1.7f)
                        .graphicsLayer { alpha = flash.value }
                        .background(
                            Brush.radialGradient(listOf(Color(0xCCFFFFFF), Color.Transparent))
                        )
                )
                // 아이콘 (좌우로 슬라이드되며 교체)
                AnimatedContent(
                    targetState = selected,
                    transitionSpec = {
                        val d = dir
                        if (d == 0) {
                            (fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.82f)) togetherWith
                                fadeOut(tween(120))
                        } else {
                            (slideInHorizontally(tween(260)) { w -> d * w / 2 } + fadeIn(tween(260))) togetherWith
                                (slideOutHorizontally(tween(200)) { w -> -d * w / 2 } + fadeOut(tween(160)))
                        }
                    },
                    label = "heroIcon"
                ) { app ->
                    if (app != null) {
                        AppIconView(
                            app = app,
                            width = iconSize * 0.76f,
                            height = iconSize * 0.76f,
                            modifier = Modifier
                                .graphicsLayer {
                                    val s = 1f + punch * 0.5f
                                    scaleX = s
                                    scaleY = s
                                }
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onLaunch,
                                    onLongClick = onMenu
                                )
                        )
                    }
                }
            }
            // 바닥 그림자
            Box(
                Modifier
                    .width(iconSize * 0.9f)
                    .height(14.dp)
                    .drawBehind {
                        val c = Offset(this.size.width / 2f, this.size.height / 2f)
                        val r = this.size.width / 2f
                        withTransform({ scale(1f, 0.12f, c) }) {
                            drawCircle(
                                Brush.radialGradient(
                                    listOf(Color(0xAA000000), Color.Transparent),
                                    center = c,
                                    radius = r
                                ),
                                radius = r,
                                center = c
                            )
                        }
                    }
            )
            Spacer(Modifier.height(4.dp))
            Text(
                selected?.pkg ?: "",
                color = SlotLine.copy(alpha = 0.55f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 이전 / 다음 버튼
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
                .border(1.dp, Color(0x66B9C6DA), CircleShape)
                .clickable { stepState(-1) },
            contentAlignment = Alignment.Center
        ) {
            Text("‹", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
                .border(1.dp, Color(0x66B9C6DA), CircleShape)
                .clickable { stepState(1) },
            contentAlignment = Alignment.Center
        ) {
            Text("›", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ───────────── 육각 슬롯 줄 (즐겨찾기) ─────────────
@Composable
private fun TeamRow(
    slots: List<String>,
    apps: List<AppEntry>,
    selectedPkg: String?,
    effects: Boolean,
    onPreview: (String) -> Unit,
    onTap: (Int, String) -> Unit,
    onLong: (Int, String) -> Unit
) {
    val cfgW = LocalConfiguration.current.screenWidthDp.dp
    val hexW = minOf(cfgW / 5f * 0.72f, 78.dp)
    val hexH = hexW * 1.14f

    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind {
                val w = this.size.width
                val y = this.size.height - 1.dp.toPx()
                val t = 12.dp.toPx()
                val p = Path().apply {
                    moveTo(0f, y - t)
                    lineTo(t, y)
                    lineTo(w - t, y)
                    lineTo(w, y - t)
                }
                drawPath(p, color = Color(0x77B9C6DA), style = Stroke(1.dp.toPx()))
            }
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        slots.forEachIndexed { index, pkg ->
            val app = apps.firstOrNull { it.pkg == pkg }
            HexSlot(
                index = index,
                pkg = pkg,
                app = app,
                isSel = app != null && app.pkg == selectedPkg,
                hexW = hexW,
                hexH = hexH,
                effects = effects,
                modifier = Modifier.weight(1f),
                onPreview = { if (app != null) onPreview(app.pkg) },
                onTap = { onTap(index, pkg) },
                onLong = { onLong(index, pkg) }
            )
        }
    }
}

@Composable
private fun HexSlot(
    index: Int,
    pkg: String,
    app: AppEntry?,
    isSel: Boolean,
    hexW: Dp,
    hexH: Dp,
    effects: Boolean,
    modifier: Modifier,
    onPreview: () -> Unit,
    onTap: () -> Unit,
    onLong: () -> Unit
) {
    // 등장 / 고정될 때 통통 튀는 팝 애니메이션
    val pop = remember { Animatable(0f) }
    LaunchedEffect(pkg) {
        pop.snapTo(if (pkg.isEmpty()) 0.6f else 0.3f)
        delay(index * 60L)
        pop.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
    }
    var pressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = tween(80),
        label = "slotPress"
    )
    val pulse: Float = if (isSel && effects) {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0.55f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
            label = "slotPulseF"
        ).value
    } else {
        1f
    }
    val line = when {
        isSel -> Orange.copy(alpha = pulse)
        app != null -> SlotBlue
        else -> SlotLine.copy(alpha = 0.6f)
    }

    Column(
        modifier.graphicsLayer {
            val s = pop.value * pressScale
            scaleX = s
            scaleY = s
            alpha = pop.value.coerceIn(0f, 1f)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(width = hexW + 8.dp, height = hexH + 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(width = hexW, height = hexH)
                    .drawBehind {
                        if (isSel) {
                            drawCircle(
                                Brush.radialGradient(
                                    listOf(Orange.copy(alpha = 0.6f * pulse), Color.Transparent)
                                ),
                                radius = this.size.maxDimension * 0.8f
                            )
                        }
                    }
                    .clip(HexShape)
                    .background(if (app != null) Color(0xFF22304A) else Color(0x66101826))
                    .border(if (isSel) 3.dp else 2.dp, line, HexShape)
                    .tapGestures(
                        onPress = onPreview,
                        onPressedChange = { pressed = it },
                        onTap = onTap,
                        onLongPress = onLong
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (app != null) {
                    AppIconView(
                        app = app,
                        width = hexW,
                        height = hexH,
                        iconScale = 0.7f
                    )
                } else {
                    Text(
                        "+",
                        color = SlotLine,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (app != null) {
                Badge(Modifier.align(Alignment.TopStart)) {
                    GroupIcon(app.group, 8.dp, Color.White)
                }
            }
            Badge(Modifier.align(Alignment.TopEnd)) {
                Text(
                    "${index + 1}",
                    color = SlotLine,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            app?.label ?: "탭해서 추가",
            color = if (app != null) NameBlue else SlotLine.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(hexW + 12.dp)
        )
    }
}

// ───────────── 하단 로스터 (그룹별) ─────────────
@Composable
private fun Roster(
    groups: List<Pair<AppGroup, List<AppEntry>>>,
    selectedPkg: String?,
    slots: List<String>,
    state: LazyListState,
    tile: Dp,
    effects: Boolean,
    onPreview: (String) -> Unit,
    onLaunch: (String) -> Unit,
    onMenu: (AppEntry) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(PanelDark)
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            state = state,
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(groups, key = { it.first.ordinal }) { gp ->
                val group = gp.first
                val list = gp.second
                Column(Modifier.width(IntrinsicSize.Max)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GroupIcon(group, 12.dp, Color.White)
                        Spacer(Modifier.width(5.dp))
                        Text(
                            group.label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("${list.size}", color = SlotLine, fontSize = 10.sp)
                    }
                    Spacer(Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        list.chunked(2).forEach { pair ->
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                pair.forEach { app ->
                                    AppTile(
                                        app = app,
                                        tile = tile,
                                        isSel = app.pkg == selectedPkg,
                                        isPinned = app.pkg in slots,
                                        effects = effects,
                                        onPreview = { onPreview(app.pkg) },
                                        onTap = { onLaunch(app.pkg) },
                                        onLongPress = { onMenu(app) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x66B9C6DA))
                    )
                    Text(
                        group.sub,
                        color = SlotLine,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTile(
    app: AppEntry,
    tile: Dp,
    isSel: Boolean,
    isPinned: Boolean,
    effects: Boolean,
    onPreview: () -> Unit,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 0.92f
            isSel -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tile"
    )
    val pulse: Float = if (isSel && effects) {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "tilePulseF"
        ).value
    } else {
        1f
    }

    Box(
        Modifier
            .size(tile)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                if (isSel) {
                    val pad = 8.dp.toPx()
                    drawRect(
                        brush = Brush.radialGradient(
                            listOf(Orange.copy(alpha = 0.45f * pulse), Color.Transparent)
                        ),
                        topLeft = Offset(-pad, -pad),
                        size = Size(this.size.width + pad * 2, this.size.height + pad * 2)
                    )
                }
            }
            .background(Color(0xFF1B263B))
            .border(
                if (isSel) 3.dp else 1.dp,
                if (isSel) Orange.copy(alpha = pulse) else Color(0x55FFFFFF)
            )
            .tapGestures(
                onPress = onPreview,
                onPressedChange = { pressed = it },
                onTap = onTap,
                onLongPress = onLongPress
            ),
        contentAlignment = Alignment.Center
    ) {
        AppIconView(
            app = app,
            width = tile,
            height = tile,
            iconScale = 0.8f,
            modifier = Modifier.alpha(if (isSel) 1f else 0.9f)
        )
        if (isPinned) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(6.dp)
                    .background(Orange, CircleShape)
            )
        }
        if (isSel) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD000000)))
                    )
                    .padding(top = 6.dp, bottom = 1.dp)
            ) {
                Text(
                    app.label,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ───────────── 검색 버튼 (빛이 훑고 지나감) ─────────────
@Composable
private fun SearchButton(effects: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(80),
        label = "btn"
    )
    val shine: State<Float>? = if (effects) {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
            label = "shineF"
        )
    } else {
        null
    }

    Row(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(48.dp)
            .background(
                Brush.verticalGradient(
                    if (pressed) listOf(OrangeDark, Color(0xFFB86A00))
                    else listOf(Color(0xFFFFB347), Orange, OrangeDark)
                )
            )
            .border(1.dp, Color(0x99FFD08A))
            .drawWithContent {
                drawContent()
                val s = shine?.value
                if (s != null) {
                    val x = -60f + s * (this.size.width + 120f)
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(Color.Transparent, Color(0x66FFFFFF), Color.Transparent),
                            start = Offset(x, 0f),
                            end = Offset(x + 70f, 30f)
                        ),
                        size = this.size
                    )
                }
            }
            .clickable(interactionSource = src, indication = null) { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchIcon(20.dp, Color.White)
        Spacer(Modifier.width(8.dp))
        Text(
            "앱 검색",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

// ───────────── 길게 누르기 메뉴 ─────────────
@Composable
private fun AppMenu(
    app: AppEntry,
    pinned: Boolean,
    onDismiss: () -> Unit,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit,
    onInfo: () -> Unit,
    onHide: () -> Unit,
    onUninstall: () -> Unit
) {
    val p = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        p.animateTo(1f, tween(220, easing = FastOutSlowInEasing))
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = p.value }
            .background(Color(0xAA000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onDismiss()
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            Modifier
                .graphicsLayer { translationY = (1f - p.value) * 160.dp.toPx() }
                .fillMaxWidth()
                .background(Color(0xF2121A28))
                .border(1.dp, Color(0x44B9C6DA))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconView(
                    app = app,
                    width = 56.dp,
                    height = 56.dp,
                    iconScale = 0.93f,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        app.label,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(app.group.label, color = Orange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            MenuButton("실행", false, onLaunch)
            MenuButton(if (pinned) "슬롯에서 해제" else "슬롯에 고정", false, onTogglePin)
            MenuButton("앱 정보", false, onInfo)
            MenuButton("숨기기", false, onHide)
            MenuButton("삭제", true, onUninstall)
            MenuButton("닫기", false, onDismiss)
        }
    }
}

@Composable
private fun MenuButton(text: String, danger: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .height(46.dp)
            .background(Color(0xFF1B263B))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (danger) Color(0xFFFF6B6B) else Orange)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text,
            color = if (danger) Color(0xFFFF8A8A) else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ───────────── 전체 앱 / 검색 서랍 ─────────────
@Composable
private fun AppDrawer(
    apps: List<AppEntry>,
    title: String,
    autoFocus: Boolean,
    onTap: (AppEntry) -> Unit,
    onLong: (AppEntry) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = remember(apps, query) { apps.filter { matchesQuery(it, query) } }
    val focus = remember { FocusRequester() }
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        if (autoFocus) {
            delay(250)
            try {
                focus.requestFocus()
            } catch (e: Exception) {
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xF20B111C))
            .systemBarsPadding()
            .imePadding()
            // 아래로 밀면 닫기
            .pointerInput(Unit) {
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = { if (total > 200f) onClose() },
                    onDragCancel = { total = 0f },
                    onVerticalDrag = { _, amount -> total += amount }
                )
            }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x66B9C6DA), CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            GhostText(title, 22.sp)
        }

        BasicTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            cursorBrush = SolidColor(Orange),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val first = results.firstOrNull()
                    if (first != null) {
                        onTap(first)
                    } else if (query.isNotBlank()) {
                        webSearch(ctx, query)
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFF1B263B))
                .border(1.dp, Orange)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .focusRequester(focus),
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SearchIcon(18.dp, SlotLine)
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                "앱 이름 검색 (초성도 가능)",
                                color = SlotLine.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        }
                        inner()
                    }
                }
            }
        )

        Text(
            "${results.size}개",
            color = SlotLine,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 2.dp)
        )

        if (query.isNotBlank()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Color(0xFF1B263B))
                    .clickable { webSearch(ctx, query) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchIcon(16.dp, IceBlue)
                Spacer(Modifier.width(10.dp))
                Text(
                    "“${query.trim()}” 웹에서 검색",
                    color = IceBlue,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(76.dp),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(results, key = { it.pkg }) { app ->
                Column(
                    Modifier
                        .padding(4.dp)
                        .tapGestures(
                            onTap = { onTap(app) },
                            onLongPress = { onLong(app) }
                        )
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppIconView(
                        app = app,
                        width = 58.dp,
                        height = 58.dp,
                        iconScale = 0.9f,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        app.label,
                        color = Color.White,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ───────────── 작은 정사각 버튼 + 아이콘 ─────────────
@Composable
private fun SideButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    Box(
        Modifier
            .size(46.dp)
            .graphicsLayer {
                val s = if (pressed) 0.92f else 1f
                scaleX = s
                scaleY = s
            }
            .background(Color(0xCC1B263B))
            .border(1.dp, Color(0x88B9C6DA))
            .clickable(interactionSource = src, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// kind: 0 = 위젯, 1 = 전체 앱, 2 = 설정
@Composable
private fun ToolIcon(kind: Int, iconSize: Dp, color: Color) {
    Canvas(Modifier.size(iconSize)) {
        val w = this.size.width
        val h = this.size.height
        when (kind) {
            0 -> {
                val g = w * 0.1f
                val c = (w - g * 3f) / 2f
                for (r in 0..1) {
                    for (q in 0..1) {
                        drawRoundRect(
                            color,
                            Offset(g + q * (c + g), g + r * (c + g)),
                            Size(c, c),
                            CornerRadius(c * 0.2f)
                        )
                    }
                }
            }
            1 -> {
                for (r in 0..2) {
                    for (q in 0..2) {
                        drawCircle(
                            color,
                            radius = w * 0.085f,
                            center = Offset(w * (0.18f + 0.32f * q), h * (0.18f + 0.32f * r))
                        )
                    }
                }
            }
            else -> {
                val cx = w / 2f
                val cy = h / 2f
                drawCircle(color, radius = w * 0.2f, center = Offset(cx, cy), style = Stroke(w * 0.11f))
                for (k in 0 until 8) {
                    val a = (k * PI / 4.0).toFloat()
                    val dx = cos(a)
                    val dy = sin(a)
                    drawLine(
                        color,
                        Offset(cx + dx * w * 0.3f, cy + dy * w * 0.3f),
                        Offset(cx + dx * w * 0.44f, cy + dy * w * 0.44f),
                        strokeWidth = w * 0.13f
                    )
                }
            }
        }
    }
}

// ───────────── 설정 ─────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        color = Orange,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 14.dp, bottom = 2.dp)
    )
}

@Composable
private fun ToggleRow(title: String, sub: String, on: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .background(Color(0xFF1B263B))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = SlotLine, fontSize = 11.sp)
        }
        Box(
            Modifier
                .size(width = 46.dp, height = 26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (on) Orange else Color(0xFF3A4660))
                .padding(3.dp),
            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun SettingsPanel(
    hasWallpaper: Boolean,
    dim: Float,
    onDim: (Float) -> Unit,
    onDimDone: () -> Unit,
    onPickWallpaper: () -> Unit,
    onResetWallpaper: () -> Unit,
    effects: Boolean,
    onToggleEffects: () -> Unit,
    hiddenCount: Int,
    onShowHidden: () -> Unit,
    onHomeSettings: () -> Unit,
    onSystemSettings: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onClose()
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .background(Color(0xF2121A28))
                .border(1.dp, Color(0x44B9C6DA))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            GhostText("설정", 26.sp)

            SectionLabel("배경화면")
            MenuButton("사진으로 배경 바꾸기", false, onPickWallpaper)
            if (hasWallpaper) {
                MenuButton("기본 배경으로 되돌리기", false, onResetWallpaper)
                Spacer(Modifier.height(8.dp))
                Text(
                    "배경 어둡기 ${(dim * 100f).toInt()}%",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = dim,
                    onValueChange = onDim,
                    valueRange = 0f..0.85f,
                    onValueChangeFinished = onDimDone,
                    colors = SliderDefaults.colors(
                        thumbColor = Orange,
                        activeTrackColor = Orange,
                        inactiveTrackColor = Color(0xFF3A4660)
                    )
                )
            }

            SectionLabel("효과")
            ToggleRow(
                "애니메이션 · 진동 · 소리",
                "끄면 배터리가 절약돼요",
                effects,
                onToggleEffects
            )

            SectionLabel("앱 관리")
            if (hiddenCount > 0) {
                MenuButton("숨긴 앱 ${hiddenCount}개 다시 표시", false, onShowHidden)
            }
            MenuButton("기본 홈 앱 설정", false, onHomeSettings)
            MenuButton("시스템 설정 열기", false, onSystemSettings)
            MenuButton("닫기", false, onClose)
        }
    }
}

// ───────────── 위젯 ─────────────
@Composable
private fun WidgetBoard(
    host: AppWidgetHost,
    ids: List<Int>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val mgr = remember { AppWidgetManager.getInstance(ctx) }
    val widthDp = (LocalConfiguration.current.screenWidthDp - 24).coerceAtLeast(120)

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xF20B111C))
            .systemBarsPadding()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x66B9C6DA), CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            GhostText("위젯", 22.sp, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .height(38.dp)
                    .background(Orange)
                    .clickable { onAdd() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("+ 추가", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            if (ids.isEmpty()) {
                Text(
                    "아직 위젯이 없어요.\n오른쪽 위 + 추가 를 눌러 시계, 날씨, 음악 위젯 등을 넣어보세요.",
                    color = SlotLine,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
                )
            }
            ids.forEach { id ->
                key(id) {
                    val info = remember(id) {
                        try {
                            mgr.getAppWidgetInfo(id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val hDp = if (info != null) {
                        with(density) { info.minHeight.toDp() }.value.coerceIn(80f, 420f)
                    } else {
                        80f
                    }
                    Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(hDp.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x22FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (info != null) {
                                AndroidView(
                                    factory = { c -> host.createView(c, id, info) },
                                    update = { v ->
                                        v.updateAppWidgetSize(null, widthDp, hDp.toInt(), widthDp, hDp.toInt())
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("사라진 위젯이에요", color = SlotLine, fontSize = 13.sp)
                            }
                        }
                        Text(
                            "제거",
                            color = Color(0xFFFF8A8A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable { onRemove(id) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WidgetPicker(onPick: (WidgetChoice) -> Unit, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val choices by produceState<List<WidgetChoice>?>(initialValue = null) {
        value = withContext(Dispatchers.Default) { loadWidgetChoices(ctx) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xF20B111C))
            .systemBarsPadding()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x66B9C6DA), CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            GhostText("위젯 고르기", 22.sp)
        }

        val list = choices
        if (list == null) {
            Text(
                "불러오는 중…",
                color = SlotLine,
                fontSize = 14.sp,
                modifier = Modifier.padding(20.dp)
            )
        } else if (list.isEmpty()) {
            Text(
                "이 기기에서 쓸 수 있는 위젯이 없어요.",
                color = SlotLine,
                fontSize = 14.sp,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(list, key = { it.key }) { w ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(Color(0xFF1B263B))
                            .clickable { onPick(w) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (w.icon != null) {
                            Image(
                                bitmap = w.icon,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        } else {
                            Spacer(Modifier.size(40.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                w.label,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                w.appLabel,
                                color = SlotLine,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
