-dontobfuscate
-optimizations !code/allocation/variable

-keepattributes *Annotation*
-keepattributes Signature

-keep class sun.misc.Unsafe { *; }

-keep class moe.feng.nyanpasu.tile.model.** { *; }
-keep class moe.feng.nyanpasu.tile.services.** { *; }
-keep class moe.feng.nyanpasu.tile.tiles.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.okhttp3.**