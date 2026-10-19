package com.example.picklauncher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

private fun formatTime(): String = SimpleDateFormat("H:mm", Locale.getDefault()).format(Date())
private fun formatDate(): String = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA).format(Date())

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
    val screenW = LocalConfiguration.current.screenWidthDp
    val tile = if (screenW >= 600) 64.dp else 52.dp
    val prefs = remember { ctx.getSharedPreferences("pick_launcher", Context.MODE_PRIVATE) }

    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var slots by remember { mutableStateOf(loadSlots(prefs)) }
    var recents by remember { mutableStateOf(loadRecents(prefs)) }
    var selectedPkg by remember { mutableStateOf<String?>(prefs.getString("last", null)) }
    var scrollTick by remember { mutableIntStateOf(0) }
    var menuApp by remember { mutableStateOf<AppEntry?>(null) }
    var drawerOpen by remember { mutableStateOf(false) }
    var drawerKeyboard by remember { mutableStateOf(true) }
    var pickSlot by remember { mutableIntStateOf(-1) }

    LaunchedEffect(resumeTick) {
        val loaded = withContext(Dispatchers.Default) { loadApps(ctx) }
        if (loaded.map { it.pkg } != apps.map { it.pkg }) apps = loaded
    }

    val clock by produceState(initialValue = Pair(formatTime(), formatDate())) {
        while (true) {
            value = Pair(formatTime(), formatDate())
            delay(1000L)
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

    // 미리보기만 (누르는 동안 중앙에 표시)
    fun select(pkg: String) {
        selectedPkg = pkg
    }

    fun step(delta: Int) {
        if (apps.isEmpty()) return
        val i = if (selectedIndex < 0) 0 else (selectedIndex + delta + apps.size) % apps.size
        select(apps[i].pkg)
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

    fun launch(pkg: String) {
        val newRecents = (listOf(pkg) + recents.filter { it != pkg }).take(RECENT_MAX)
        recents = newRecents
        selectedPkg = pkg
        prefs.edit()
            .putString("recent", newRecents.joinToString("|"))
            .putString("last", pkg)
            .apply()
        launchApp(ctx, pkg)
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
        BackgroundDecor()

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
            Header(
                selected = selected,
                total = apps.size,
                index = selectedIndex,
                time = clock.first,
                date = clock.second
            )

            Hero(
                selected = selected,
                onStep = { d -> step(d) },
                onLaunch = { selected?.let { launch(it.pkg) } },
                onMenu = { selected?.let { menuApp = it } },
                modifier = Modifier.weight(1f)
            )

            TeamRow(
                slots = slots,
                apps = apps,
                selectedPkg = selected?.pkg,
                onPreview = { select(it) },
                onTap = { index, pkg ->
                    if (pkg.isEmpty()) openDrawer(false, index) else launch(pkg)
                },
                onLong = { index, pkg ->
                    val app = apps.firstOrNull { it.pkg == pkg }
                    if (app != null) menuApp = app else openDrawer(false, index)
                }
            )

            Spacer(Modifier.height(12.dp))

            Roster(
                groups = rosterGroups,
                selectedPkg = selected?.pkg,
                slots = slots,
                state = rosterState,
                tile = tile,
                onPreview = { select(it) },
                onLaunch = { launch(it) },
                onMenu = { menuApp = it }
            )

            Spacer(Modifier.height(12.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SearchButton { openDrawer(true, -1) }
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
            enter = slideInVertically(tween(220)) { it } + fadeIn(tween(220)),
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
                        launch(app.pkg)
                    }
                },
                onLong = { app -> menuApp = app },
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
                    launch(app.pkg)
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
    }
}

// ───────────── 탭 / 누름 / 길게 누름 한 번에 ─────────────
@Composable
private fun Modifier.tapGestures(
    onPress: () -> Unit = {},
    onTap: () -> Unit,
    onLongPress: () -> Unit
): Modifier {
    val press by rememberUpdatedState(onPress)
    val tap by rememberUpdatedState(onTap)
    val long by rememberUpdatedState(onLongPress)
    return this.pointerInput(Unit) {
        detectTapGestures(
            onPress = { press() },
            onTap = { tap() },
            onLongPress = { long() }
        )
    }
}

// ───────────── 배경 장식 ─────────────
@Composable
private fun BackgroundDecor() {
    Canvas(Modifier.fillMaxSize()) {
        val w = this.size.width
        val h = this.size.height

        val beam1 = Path().apply {
            moveTo(w * 0.50f, 0f)
            lineTo(w * 0.80f, 0f)
            lineTo(w * 0.30f, h)
            lineTo(w * 0.00f, h)
            close()
        }
        drawPath(beam1, Brush.verticalGradient(listOf(Color(0x16FFFFFF), Color.Transparent)))

        val beam2 = Path().apply {
            moveTo(w * 0.85f, 0f)
            lineTo(w * 1.00f, 0f)
            lineTo(w * 0.70f, h)
            lineTo(w * 0.55f, h)
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

// ───────────── 헤더 ─────────────
@Composable
private fun Header(selected: AppEntry?, total: Int, index: Int, time: String, date: String) {
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
            // 좌측: 타이틀 + 시계
            Column(Modifier.weight(1f)) {
                GhostText("런처", 36.sp)
                Text(
                    "앱을 선택하십시오",
                    color = IceBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
                Spacer(Modifier.height(10.dp))
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
                        Text(
                            time,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(date, color = SlotLine, fontSize = 11.sp)
                    }
                }
            }

            // 우측: 선택한 앱 이름 + 분류
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                val name = selected?.label ?: "-"
                val nameSize = when {
                    name.length <= 8 -> 32.sp
                    name.length <= 14 -> 24.sp
                    else -> 18.sp
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "+",
                        color = Orange,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.width(4.dp))
                    GhostText(name, nameSize, align = TextAlign.End)
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
    onStep: (Int) -> Unit,
    onLaunch: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepState by rememberUpdatedState(onStep)

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
                // 발광
                Box(
                    Modifier
                        .size(iconSize * 1.5f)
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
                Crossfade(targetState = selected, label = "hero") { app ->
                    if (app != null) {
                        Image(
                            bitmap = app.icon,
                            contentDescription = app.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(iconSize)
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
            val isSel = app != null && app.pkg == selectedPkg
            val line = when {
                isSel -> Orange
                app != null -> SlotBlue
                else -> SlotLine.copy(alpha = 0.6f)
            }

            Column(
                Modifier.weight(1f),
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
                                            listOf(Color(0x99F99E1A), Color.Transparent)
                                        ),
                                        radius = this.size.maxDimension * 0.8f
                                    )
                                }
                            }
                            .clip(HexShape)
                            .background(if (app != null) Color(0xFF22304A) else Color(0x66101826))
                            .border(if (isSel) 3.dp else 2.dp, line, HexShape)
                            .tapGestures(
                                onPress = { if (app != null) onPreview(app.pkg) },
                                onTap = { onTap(index, pkg) },
                                onLongPress = { onLong(index, pkg) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app != null) {
                            Image(
                                bitmap = app.icon,
                                contentDescription = app.label,
                                modifier = Modifier.size(hexW * 0.62f)
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
    onPreview: () -> Unit,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSel) 1.08f else 1f,
        animationSpec = tween(120),
        label = "tile"
    )
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
                            listOf(Color(0x66F99E1A), Color.Transparent)
                        ),
                        topLeft = Offset(-pad, -pad),
                        size = Size(this.size.width + pad * 2, this.size.height + pad * 2)
                    )
                }
            }
            .background(Color(0xFF1B263B))
            .border(
                if (isSel) 3.dp else 1.dp,
                if (isSel) Orange else Color(0x55FFFFFF)
            )
            .tapGestures(onPress = onPreview, onTap = onTap, onLongPress = onLongPress),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = Modifier
                .size(tile * 0.8f)
                .alpha(if (isSel) 1f else 0.85f)
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

// ───────────── 검색 버튼 ─────────────
@Composable
private fun SearchButton(onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(80),
        label = "btn"
    )
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
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onDismiss()
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color(0xF2121A28))
                .border(1.dp, Color(0x44B9C6DA))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    bitmap = app.icon,
                    contentDescription = app.label,
                    modifier = Modifier.size(52.dp)
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
                    Image(
                        bitmap = app.icon,
                        contentDescription = app.label,
                        modifier = Modifier.size(52.dp)
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
