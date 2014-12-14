package chip8;

import java.util.Random

data class VmState (val ram: ByteArray = ByteArray(4096),
                    val registers: IntArray = IntArray(16),
                    var pc: Int = 0x200,
                    var index: Int = 0,
                    var sp: Int = 0,
                    val stack: IntArray = IntArray(16),
                    var delay: Int = 0,
                    var sound: Int = 0,
                    val keys: IntArray = IntArray(16),
                    val vram: ByteArray = ByteArray(64*32),
                    var vramChanged: Boolean = false,
                    val rand: Random = Random(),
                    val programSize: Int) {
    {
        val fontData = intArray(
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        )
        var i = 0
        for(b in fontData) {
            ram[i++] = b.and(0xff).toByte()
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.line("PC: 0x${pc.hex} ${pc}")
        builder.line("Index: 0x${index.hex} ${index}")
        builder.line("Delay: ${delay}")
        builder.line("Sound: ${sound}")
        for(i in registers.indices)
            builder.line("V${i.hex.toUpperCase()}: 0x${registers[i].hex} ${registers[i]}")
        builder.line("Stack Pointer: 0x${sp.hex} ${sp}")
        for(i in 0..sp)
            builder.line("Stack[${i}]: 0x${stack[i].hex} ${stack[i]}")
        return builder.toString()
    }

    fun vramToString(): String {
        val builder = StringBuilder()
        for(y in 0..31) {
            for (x in 0..63) {
                builder.append(if (vram[x + y * 64].toInt() != 0)  "*" else " ")
            }
            builder.line("")
        }
        return builder.toString()
    }
}