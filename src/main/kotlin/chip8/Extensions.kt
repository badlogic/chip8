package chip8;

val Int.hex: String get() = Integer.toHexString(this)
val Byte.hex: String get() = Integer.toHexString(this.toInt())
val Byte.hi: Int get() = (this.toInt() and 0xf0) shr 4
val Byte.lo: Int get() = this.toInt() and 0xf
fun address(msb: Byte, lsb: Byte) = ((msb.toInt() and 0xf) shl 8) or (lsb.toInt() and 0xff)

fun StringBuilder.line(line: String) {
    this.append(line); this.append("\n")
}