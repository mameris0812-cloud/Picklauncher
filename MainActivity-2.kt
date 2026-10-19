package com.example.picklauncher

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

// 영웅 모드 (앱 아이콘을 캐릭터 초상화로 표시)
private var heroModeOn by mutableStateOf(true)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                BackHandler { /* 홈 화면이므로 뒤로가기 무시 */ }
                LauncherScreen(resumeTick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTick++ // 앱 설치/삭제 후 돌아오면 목록 갱신
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
                    ri.loadIcon(pm).toBitmap(128, 128).asImageBitmap()
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
fun LauncherScreen(resumeTick: Int) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val screenW = LocalConfiguration.current.screenWidthDp
    val tile = if (screenW >= 600) 64.dp else 52.dp
    val prefs = remember { ctx.getSharedPreferences("pick_launcher", Context.MODE_PRIVATE) }
    remember { heroModeOn = prefs.getBoolean("hero", true) }

    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
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
    val launchFx = remember { Animatable(0f) }

    LaunchedEffect(resumeTick) {
        val loaded = withContext(Dispatchers.Default) { loadApps(ctx) }
        if (loaded.map { it.pkg } != apps.map { it.pkg }) apps = loaded
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

    fun toggleHero() {
        heroModeOn = !heroModeOn
        prefs.edit().putBoolean("hero", heroModeOn).apply()
        Toast.makeText(
            ctx,
            if (heroModeOn) "영웅 모드 켜짐" else "영웅 모드 꺼짐 (원래 아이콘)",
            Toast.LENGTH_SHORT
        ).show()
    }

    BackHandler(enabled = menuApp != null || drawerOpen) {
        if (menuApp != null) menuApp = null else closeDrawer()
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
        BackgroundDecor(effects)

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                // 위로 밀면 전체 앱
                .pointerInput(drawerOpen) {
                    var total = 0f
                    detectVerticalDragGestures(
                        onDragStart = { total = 0f },
                        onDragEnd = {
                            if (!drawerOpen && total < -160f) openDrawer(false, -1)
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
                    onToggleEffects = { toggleEffects() },
                    onToggleHero = { toggleHero() }
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

            Box(
                Modifier
                    .fillMaxWidth()
                    .introSlide(400, 90.dp),
                contentAlignment = Alignment.Center
            ) {
                SearchButton(effects) { openDrawer(true, -1) }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "탭: 실행   ·   길게 누르기: 메뉴   ·   위로 밀기: 전체 앱",
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

// ───────────── 영웅 초상화 생성 (앱 패키지명 기반, 오리지널 캐릭터) ─────────────
private val SkinTones = listOf(
    Color(0xFFFFDBB4), Color(0xFFF1C27D), Color(0xFFE0AC69),
    Color(0xFFC68642), Color(0xFF8D5524), Color(0xFF5C3A21)
)
private val HairColors = listOf(
    Color(0xFF16161A), Color(0xFF3B2416), Color(0xFF6B4423), Color(0xFFD9B15B),
    Color(0xFFC4472D), Color(0xFFE8E8F0), Color(0xFF3A5BA0), Color(0xFFD05A9A)
)
private val EyeColors = listOf(
    Color(0xFF3B7DD8), Color(0xFF2E8B57), Color(0xFF6B4423),
    Color(0xFF7A8794), Color(0xFFC08A2E)
)

private class HeroSpec(
    val skin: Color,
    val skinShade: Color,
    val hair: Color,
    val brow: Color,
    val eye: Color,
    val outfit: Color,
    val outfitDark: Color,
    val accent: Color,
    val bgTop: Color,
    val bgBottom: Color,
    val hairStyle: Int,
    val accessory: Int,
    val fierce: Float,
    val smile: Boolean
)

private fun mixBits(x: Int): Int {
    var h = x
    h = h xor (h ushr 16)
    h *= 0x85ebca6b.toInt()
    h = h xor (h ushr 13)
    h *= 0xc2b2ae35.toInt()
    h = h xor (h ushr 16)
    return h
}

private fun heroSpec(pkg: String): HeroSpec {
    val a = mixBits(pkg.hashCode())
    val b = mixBits(a + 0x1234567)
    fun pa(shift: Int, mod: Int): Int = ((a ushr shift) and 0xFF) % mod
    fun pb(shift: Int, mod: Int): Int = ((b ushr shift) and 0xFF) % mod

    val skin = SkinTones[pa(0, SkinTones.size)]
    val hair = HairColors[pa(8, HairColors.size)]
    val hue = (((a ushr 16) and 0x1FF) % 360).toFloat()
    val accentHue = (hue + 140f + pb(0, 80)) % 360f
    val bgHue = (hue + 200f) % 360f

    return HeroSpec(
        skin = skin,
        skinShade = lerp(skin, Color.Black, 0.2f),
        hair = hair,
        brow = lerp(hair, Color.Black, 0.35f),
        eye = EyeColors[pb(8, EyeColors.size)],
        outfit = Color.hsv(hue, 0.6f, 0.78f),
        outfitDark = Color.hsv(hue, 0.7f, 0.42f),
        accent = Color.hsv(accentHue, 0.75f, 0.95f),
        bgTop = Color.hsv(bgHue, 0.55f, 0.26f),
        bgBottom = Color.hsv(bgHue, 0.45f, 0.5f),
        hairStyle = pb(16, 7),
        accessory = pb(24, 6),
        fierce = 0.004f * pa(24, 6),
        smile = pa(20, 2) == 0
    )
}

private fun DrawScope.drawHero(sp: HeroSpec, w: Float, h: Float) {
    val s = minOf(w, h)
    val ox = (w - s) / 2f
    val oy = h - s
    fun px(u: Float): Float = ox + u * s
    fun py(v: Float): Float = oy + v * s
    fun pt(u: Float, v: Float): Offset = Offset(px(u), py(v))
    fun sz(u: Float, v: Float): Size = Size(u * s, v * s)

    // 배경 + 스포트라이트
    drawRect(Brush.verticalGradient(listOf(sp.bgTop, sp.bgBottom)), size = Size(w, h))
    drawCircle(
        Brush.radialGradient(
            listOf(Color(0x66FFFFFF), Color.Transparent),
            center = pt(0.5f, 0.4f),
            radius = s * 0.55f
        ),
        radius = s * 0.55f,
        center = pt(0.5f, 0.4f)
    )
    // 대각선 포인트 띠
    drawLine(sp.accent.copy(alpha = 0.35f), pt(0.05f, 0.2f), pt(0.30f, 0.0f), strokeWidth = s * 0.05f)
    drawLine(sp.accent.copy(alpha = 0.25f), pt(0.80f, 0.15f), pt(0.98f, 0.02f), strokeWidth = s * 0.035f)

    // 뒤쪽 머리 / 후드 뒤
    if (sp.hairStyle == 1) {
        val back = Path().apply {
            moveTo(px(0.30f), py(0.38f))
            cubicTo(px(0.20f), py(0.10f), px(0.80f), py(0.10f), px(0.70f), py(0.38f))
            lineTo(px(0.78f), py(0.86f))
            lineTo(px(0.22f), py(0.86f))
            close()
        }
        drawPath(back, sp.hair)
    }
    if (sp.hairStyle == 6) {
        drawOval(sp.outfitDark, pt(0.27f, 0.15f), sz(0.46f, 0.54f))
    }

    // 몸 (어깨)
    val body = Path().apply {
        moveTo(px(-0.15f), py(1.5f))
        lineTo(px(-0.08f), py(0.92f))
        cubicTo(px(0.02f), py(0.78f), px(0.22f), py(0.72f), px(0.36f), py(0.70f))
        lineTo(px(0.64f), py(0.70f))
        cubicTo(px(0.78f), py(0.72f), px(0.98f), py(0.78f), px(1.08f), py(0.92f))
        lineTo(px(1.15f), py(1.5f))
        close()
    }
    drawPath(
        body,
        Brush.verticalGradient(
            listOf(sp.outfit, sp.outfitDark),
            startY = py(0.70f),
            endY = py(1.05f)
        )
    )
    // 어깨 장식 / 줄무늬
    drawCircle(sp.accent, radius = s * 0.05f, center = pt(0.13f, 0.88f))
    drawCircle(sp.accent, radius = s * 0.05f, center = pt(0.87f, 0.88f))
    drawLine(sp.accent, pt(0.20f, 0.82f), pt(0.33f, 0.74f), strokeWidth = s * 0.028f, cap = StrokeCap.Round)
    drawLine(sp.accent, pt(0.80f, 0.82f), pt(0.67f, 0.74f), strokeWidth = s * 0.028f, cap = StrokeCap.Round)
    // 목 + 브이넥
    drawRect(sp.skinShade, pt(0.43f, 0.58f), sz(0.14f, 0.16f))
    val vneck = Path().apply {
        moveTo(px(0.39f), py(0.70f))
        lineTo(px(0.61f), py(0.70f))
        lineTo(px(0.50f), py(0.86f))
        close()
    }
    drawPath(vneck, sp.skinShade)

    // 귀 + 얼굴
    drawOval(sp.skin, pt(0.315f, 0.40f), sz(0.05f, 0.09f))
    drawOval(sp.skin, pt(0.635f, 0.40f), sz(0.05f, 0.09f))
    drawOval(sp.skin, pt(0.33f, 0.20f), sz(0.34f, 0.42f))
    drawOval(
        Brush.radialGradient(
            listOf(Color(0x22FFFFFF), Color.Transparent),
            center = pt(0.45f, 0.36f),
            radius = s * 0.26f
        ),
        pt(0.33f, 0.20f),
        sz(0.34f, 0.42f)
    )

    // 앞머리 / 후드
    when (sp.hairStyle) {
        0, 1, 4, 5 -> {
            val front = Path().apply {
                moveTo(px(0.32f), py(0.42f))
                cubicTo(px(0.28f), py(0.12f), px(0.72f), py(0.12f), px(0.68f), py(0.42f))
                lineTo(px(0.64f), py(0.34f))
                quadraticBezierTo(px(0.52f), py(0.24f), px(0.36f), py(0.34f))
                close()
            }
            drawPath(front, sp.hair)
        }
        2 -> {
            val spiky = Path().apply {
                moveTo(px(0.32f), py(0.42f))
                lineTo(px(0.29f), py(0.22f))
                lineTo(px(0.37f), py(0.27f))
                lineTo(px(0.41f), py(0.10f))
                lineTo(px(0.48f), py(0.24f))
                lineTo(px(0.54f), py(0.08f))
                lineTo(px(0.60f), py(0.24f))
                lineTo(px(0.69f), py(0.19f))
                lineTo(px(0.68f), py(0.42f))
                lineTo(px(0.64f), py(0.33f))
                quadraticBezierTo(px(0.52f), py(0.25f), px(0.36f), py(0.33f))
                close()
            }
            drawPath(spiky, sp.hair)
        }
        3 -> {
            val mohawk = Path().apply {
                moveTo(px(0.44f), py(0.26f))
                lineTo(px(0.46f), py(0.05f))
                lineTo(px(0.54f), py(0.05f))
                lineTo(px(0.56f), py(0.26f))
                close()
            }
            drawPath(mohawk, sp.hair)
            drawRect(sp.hair.copy(alpha = 0.35f), pt(0.34f, 0.22f), sz(0.32f, 0.07f))
        }
        6 -> {
            val hood = Path().apply {
                moveTo(px(0.31f), py(0.42f))
                cubicTo(px(0.29f), py(0.12f), px(0.71f), py(0.12f), px(0.69f), py(0.42f))
                lineTo(px(0.64f), py(0.31f))
                quadraticBezierTo(px(0.50f), py(0.21f), px(0.36f), py(0.31f))
                close()
            }
            drawPath(hood, sp.outfit)
            drawLine(sp.outfitDark, pt(0.33f, 0.40f), pt(0.30f, 0.62f), strokeWidth = s * 0.02f)
            drawLine(sp.outfitDark, pt(0.67f, 0.40f), pt(0.70f, 0.62f), strokeWidth = s * 0.02f)
        }
    }
    if (sp.hairStyle == 4) {
        drawCircle(sp.hair, radius = s * 0.07f, center = pt(0.5f, 0.10f))
    }
    if (sp.hairStyle == 5) {
        val tail = Path().apply {
            moveTo(px(0.66f), py(0.26f))
            cubicTo(px(0.84f), py(0.28f), px(0.88f), py(0.52f), px(0.76f), py(0.70f))
            lineTo(px(0.71f), py(0.64f))
            cubicTo(px(0.78f), py(0.50f), px(0.76f), py(0.38f), px(0.66f), py(0.34f))
            close()
        }
        drawPath(tail, sp.hair)
    }

    // 눈 / 눈썹
    val eyeY = 0.43f
    for (sx in listOf(0.425f, 0.575f)) {
        drawOval(Color.White, pt(sx - 0.032f, eyeY - 0.017f), sz(0.064f, 0.034f))
        drawCircle(sp.eye, radius = s * 0.015f, center = pt(sx, eyeY))
        drawCircle(Color(0xFF101010), radius = s * 0.008f, center = pt(sx, eyeY))
        drawCircle(Color.White, radius = s * 0.0045f, center = pt(sx - 0.005f, eyeY - 0.006f))
    }
    val br = sp.fierce
    drawLine(sp.brow, pt(0.385f, 0.385f - br), pt(0.46f, 0.398f + br), strokeWidth = s * 0.014f, cap = StrokeCap.Round)
    drawLine(sp.brow, pt(0.54f, 0.398f + br), pt(0.615f, 0.385f - br), strokeWidth = s * 0.014f, cap = StrokeCap.Round)

    // 코 / 입
    drawLine(sp.skinShade, pt(0.50f, 0.45f), pt(0.488f, 0.50f), strokeWidth = s * 0.012f, cap = StrokeCap.Round)
    drawLine(sp.skinShade, pt(0.488f, 0.50f), pt(0.512f, 0.505f), strokeWidth = s * 0.012f, cap = StrokeCap.Round)
    val mouth = Path().apply {
        moveTo(px(0.45f), py(0.545f))
        quadraticBezierTo(
            px(0.50f),
            py(if (sp.smile) 0.585f else 0.552f),
            px(0.55f),
            py(if (sp.smile) 0.545f else 0.535f)
        )
    }
    drawPath(mouth, Color(0xFF7A3B3B), style = Stroke(width = s * 0.012f, cap = StrokeCap.Round))

    // 장비 / 액세서리
    when (sp.accessory) {
        1 -> { // 바이저
            drawRoundRect(
                sp.accent.copy(alpha = 0.9f),
                pt(0.335f, 0.395f),
                sz(0.33f, 0.075f),
                CornerRadius(s * 0.03f, s * 0.03f)
            )
            drawLine(Color(0x99FFFFFF), pt(0.36f, 0.412f), pt(0.46f, 0.412f), strokeWidth = s * 0.008f, cap = StrokeCap.Round)
        }
        2 -> { // 헤드셋
            val band = Path().apply {
                moveTo(px(0.32f), py(0.42f))
                cubicTo(px(0.30f), py(0.08f), px(0.70f), py(0.08f), px(0.68f), py(0.42f))
            }
            drawPath(band, sp.outfitDark, style = Stroke(width = s * 0.025f, cap = StrokeCap.Round))
            drawCircle(sp.outfit, radius = s * 0.04f, center = pt(0.315f, 0.46f))
            drawCircle(sp.outfit, radius = s * 0.04f, center = pt(0.685f, 0.46f))
            drawCircle(sp.accent, radius = s * 0.018f, center = pt(0.315f, 0.46f))
            drawCircle(sp.accent, radius = s * 0.018f, center = pt(0.685f, 0.46f))
            drawLine(sp.outfitDark, pt(0.33f, 0.50f), pt(0.42f, 0.56f), strokeWidth = s * 0.01f, cap = StrokeCap.Round)
            drawCircle(sp.accent, radius = s * 0.014f, center = pt(0.42f, 0.56f))
        }
        3 -> { // 하관 마스크
            val mask = Path().apply {
                moveTo(px(0.34f), py(0.49f))
                lineTo(px(0.66f), py(0.49f))
                cubicTo(px(0.66f), py(0.64f), px(0.34f), py(0.64f), px(0.34f), py(0.49f))
                close()
            }
            drawPath(mask, sp.outfitDark)
            drawLine(sp.accent, pt(0.36f, 0.55f), pt(0.64f, 0.55f), strokeWidth = s * 0.012f)
        }
        4 -> { // 안경
            for (sx in listOf(0.425f, 0.575f)) {
                drawCircle(Color(0xCC111111), radius = s * 0.05f, center = pt(sx, eyeY), style = Stroke(width = s * 0.011f))
            }
            drawLine(Color(0xCC111111), pt(0.475f, eyeY), pt(0.525f, eyeY), strokeWidth = s * 0.01f)
        }
        5 -> { // 페이스 페인트 + 흉터
            drawLine(sp.accent, pt(0.37f, 0.49f), pt(0.45f, 0.52f), strokeWidth = s * 0.016f, cap = StrokeCap.Round)
            drawLine(sp.accent, pt(0.63f, 0.49f), pt(0.55f, 0.52f), strokeWidth = s * 0.016f, cap = StrokeCap.Round)
            drawLine(Color(0xAAA04848), pt(0.60f, 0.35f), pt(0.64f, 0.45f), strokeWidth = s * 0.008f, cap = StrokeCap.Round)
        }
    }

    // 하단 비네팅
    drawRect(
        Brush.verticalGradient(
            listOf(Color.Transparent, Color(0x66000000)),
            startY = py(0.75f),
            endY = h
        ),
        size = Size(w, h)
    )
}

// 앱 아이콘 → (영웅 모드면) 캐릭터 초상화 + 실제 앱 아이콘은 엠블럼 배지
@Composable
private fun AppIconView(
    app: AppEntry,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    iconScale: Float = 1f,
    badge: Float = 0.38f,
    badgeEndInset: Dp = 0.dp,
    badgeBottomInset: Dp = 0.dp
) {
    if (!heroModeOn) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = modifier.size(minOf(width, height) * iconScale)
        )
    } else {
        val spec = remember(app.pkg) { heroSpec(app.pkg) }
        val badgeSize = minOf(width, height) * badge
        Box(modifier.size(width, height).clipToBounds()) {
            Canvas(Modifier.fillMaxSize()) {
                drawHero(spec, this.size.width, this.size.height)
            }
            Image(
                bitmap = app.icon,
                contentDescription = app.label,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = badgeEndInset, bottom = badgeBottomInset)
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(Color(0xFF0E1522))
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
    }
}

// ───────────── 시계 (콜론 깜빡임) ─────────────
@Composable
private fun ClockBlock() {
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(500L)
        }
    }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(h, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(
                    ":",
                    color = Color.White.copy(alpha = if (colonOn) 1f else 0.2f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(m, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Text(d, color = SlotLine, fontSize = 11.sp)
        }
    }
}

// ───────────── 헤더 ─────────────
@Composable
private fun Header(
    selected: AppEntry?,
    total: Int,
    index: Int,
    onToggleEffects: () -> Unit,
    onToggleHero: () -> Unit
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
            // 좌측: 타이틀(길게 누르면 효과 on/off) + 시계
            Column(Modifier.weight(1f)) {
                GhostText(
                    "런처",
                    36.sp,
                    modifier = Modifier.tapGestures(onTap = {}, onLongPress = onToggleEffects)
                )
                Text(
                    "앱을 선택하십시오",
                    color = IceBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.tapGestures(onTap = {}, onLongPress = onToggleHero)
                )
                Spacer(Modifier.height(10.dp))
                ClockBlock()
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
                        val hm = heroModeOn
                        val iw = if (hm) iconSize * 1.08f else iconSize
                        val ih = if (hm) iconSize * 1.24f else iconSize
                        AppIconView(
                            app = app,
                            width = iw,
                            height = ih,
                            badge = 0.3f,
                            badgeEndInset = if (hm) iw * 0.17f else 0.dp,
                            badgeBottomInset = if (hm) ih * 0.13f else 0.dp,
                            modifier = Modifier
                                .then(if (hm) Modifier.clip(HexShape) else Modifier)
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
                // 초상화 위로 육각 테두리
                if (heroModeOn) {
                    Box(
                        Modifier
                            .size(width = iconSize * 1.08f, height = iconSize * 1.24f)
                            .clip(HexShape)
                            .border(2.dp, SlotBlue.copy(alpha = 0.85f), HexShape)
                    )
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
                        iconScale = 0.62f,
                        badge = 0.34f,
                        badgeEndInset = hexW * 0.17f,
                        badgeBottomInset = hexH * 0.12f
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
            badge = 0.42f,
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
private fun SearchButton(effects: Boolean, onClick: () -> Unit) {
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
        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .width(220.dp)
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
                    badge = 0.4f,
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
            keyboardActions = KeyboardActions(onSearch = { results.firstOrNull()?.let(onTap) }),
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
                        badge = 0.4f,
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
