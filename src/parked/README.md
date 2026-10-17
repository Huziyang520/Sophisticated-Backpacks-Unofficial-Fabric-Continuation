# 暂存（未参与编译）的源码

这里存放本轮**无法在 26.3 Fabric 上编译**的兼容层与渲染实现。语法完整、逻辑未改，只是不在 Loom 的源集里。

## 为什么放在这里

| 包 | 停用原因 |
|---|---|
| `compat/chipped` | Chipped 无 26.3 Fabric 编译期 API，且核心仓的 `sophisticatedcore.compat.chipped` 也已 park |
| `compat/emi` | EMI 无 26.3 版；核心仓 `compat.emi` 已 park |
| `compat/trinkets` | Trinkets 无 26.3 版；核心仓 `compat.trinkets` 已 park |
| `compat/litematica`（+ mixins json） | 核心仓 `compat.litematica` 已 park，且只有 CurseMaven 有产物，本机不可达 |
| `compat/rei` | REI 26.3 的 `REIServerPlugin` 等 API 变更 + 核心仓 `compat.rei` 已 park |
| `client/render/*` | 26.3 删除了 `BakedModel` / `BakedQuad` / `MultiBufferSource` / `RenderType` / `ItemOverrides` 等渲染模型 API，核心仓同层已 park |

数据生成（datagen）的 provider 已删除，产物固化在 `src/generated/resources`（已作为资源根接入 `build.gradle`）。

## 怎么接回来

1. 确认该模组 API 可作为编译期依赖解析（`build.gradle` 的 `compileOnly` 或 `libs/`）。
2. 把目录搬回 `src/main/java/net/p3pp3rf1y/sophisticatedbackpacks/` 对应位置。
3. `init/ModCompat.register()` 里补一行 `registry.registerCompat(new CompatInfo(CompatModIds.XXX, null), () -> new XXXCompat());`。
4. Litematica 还要把 `resources/compat/sophisticatedbackpacks.litematica.mixins.json` 放回 `src/main/resources/compat/` 并写回 `fabric.mod.json` 的 `mixins` 列表。
5. REI 需要把 `rei_common` / `rei_client` 入口写回 `fabric.mod.json`。
