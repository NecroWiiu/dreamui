# Dream UI (Fabric 1.20.1, Java 17)

## Build
1. Install JDK 17 and Gradle 8.6+ (or open the folder in IntelliJ IDEA).
2. `gradle wrapper --gradle-version 8.6` (once), then `./gradlew build`
3. The mod is `build/libs/dreamui-1.0.0.jar` -> put it in `.minecraft/mods`.

Requires Fabric Loader 0.14.21+ and Fabric API. Mod Menu (included in Fabulously Optimized) powers the "Mods" button.

## Music
Copyright prevents bundling the song. Put your own legally obtained file, converted to OGG Vorbis, at:
`src/main/resources/assets/dreamui/sounds/music/bam_bam.ogg`
and rebuild (or drop it in a resource pack at `assets/dreamui/sounds/music/bam_bam.ogg` - no rebuild needed).
Until the file exists, the normal Minecraft music is untouched.

## Notes
- Buttons/sliders in every screen are restyled, except Video/Graphics/Shader screens (Sodium, Iris, Reese's, vanilla Video Settings).
- Change the background by replacing `assets/dreamui/textures/gui/background.png` (update TEX_W/TEX_H in DreamUI.java).
