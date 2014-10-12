package chip8;

import java.io.DataInputStream
import java.io.BufferedInputStream
import java.io.FileInputStream

fun loadRom(file: String): VmState {
    return DataInputStream(BufferedInputStream(FileInputStream(file))).use {
        val rom = it.readBytes()
        val state = VmState(programSize=rom.size)
        System.arraycopy(rom, 0, state.ram, state.pc, rom.size)
        state
    }
}

fun decode(decoder: Decoder, address:Int, msb: Byte, lsb: Byte) {
    val opCode = (msb.toInt() shl 8 or lsb.toInt().and(0xff)).and(0xffff)
    decoder.before(opCode, address)
    when (msb.hi) {
        0x0 -> {
            when (msb.toInt() shl 8 or lsb.toInt()) {
                0x00e0 -> decoder.clear()
                0x00ee -> decoder.ret()
                else -> decoder.unknown(opCode, address)
            }
        }
        0x1 -> decoder.jmp(address(msb, lsb))
        0x2 -> decoder.call(address(msb, lsb))
        0x3 -> decoder.jeq(msb.lo, lsb.toInt())
        0x4 -> decoder.jneq(msb.lo, lsb.toInt())
        0x5 -> decoder.jeqr(msb.lo, lsb.hi)
        0x6 -> decoder.set(msb.lo, lsb.toInt())
        0x7 -> decoder.add(msb.lo, lsb.toInt())
        0x8 -> {
            val reg1 = msb.lo
            val reg2 = lsb.hi
            when(lsb.lo) {
                0x0 -> decoder.setr(reg1, reg2)
                0x1 -> decoder.or(reg1, reg2)
                0x2 -> decoder.and(reg1, reg2)
                0x3 -> decoder.xor(reg1, reg2)
                0x4 -> decoder.addr(reg1, reg2)
                0x5 -> decoder.sub(reg1, reg2)
                0x6 -> decoder.shr(reg1)
                0x7 -> decoder.subb(reg1, reg2)
                0xe -> decoder.shl(reg1)
                else -> decoder.unknown(opCode, address)
            }
        }
        0x9 -> {
            val reg1 = msb.lo
            val reg2 = lsb.hi
            decoder.jneqr(reg1, reg2)
        }
        0xa -> decoder.seti(address(msb, lsb))
        0xb -> decoder.jmpv0(address(msb, lsb))
        0xc -> decoder.rand(msb.lo, lsb.toInt())
        0xd -> decoder.draw(msb.lo, lsb.hi, lsb.lo)
        0xe -> {
            when(lsb.toInt() or 0xff) {
                0x9e -> decoder.jkey(msb.lo)
                0xa1 -> decoder.jnkey(msb.lo)
                else -> decoder.unknown(opCode, address)
            }
        }
        0xf -> {
            val reg = msb.lo
            when(lsb.toInt() or 0xff) {
                0x07 -> decoder.getdelay(reg)
                0x0a -> decoder.waitkey(reg)
                0x15 -> decoder.setdelay(reg)
                0x18 -> decoder.setsound(reg)
                0x1e -> decoder.addi(reg)
                0x29 -> decoder.spritei(reg)
                0x33 -> decoder.bcd(reg)
                0x55 -> decoder.push(reg)
                0x65 -> decoder.pop(reg)
                else -> decoder.unknown(opCode, address)
            }
        }
        else -> decoder.unknown(opCode, address)
    }
}

fun disassemble(vmState: VmState): String {
    val decoder = Disassembler()
    for(addr in 0x200..(0x200+vmState.programSize - 1) step 2) {
        val msb = vmState.ram[addr]
        val lsb = vmState.ram[addr + 1]
        decode(decoder, addr, msb, lsb)
    }
    return decoder.toString()
}