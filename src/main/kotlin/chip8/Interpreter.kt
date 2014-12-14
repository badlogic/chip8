package chip8

import java.util.Arrays

class Interpreter(val state: VmState): Decoder {

    fun step() {
        val msb = state.ram[state.pc]
        val lsb = state.ram[state.pc + 1]
        chip8.decode(this, state.pc, msb, lsb)
    }

    override fun before(opCode: Int, address: Int) {
        state.vramChanged = false
    }

    override fun unknown(opCode: Int, address: Int) {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        Arrays.fill(state.vram, 0)
        state.vramChanged = true
    }

    override fun ret() {
        state.pc = state.stack[state.sp].and(0xffff)
        state.sp--
    }

    override fun jmp(address: Int) {
        state.pc = address.and(0xffff)
    }

    override fun call(address: Int) {
        state.sp++
        state.stack[state.sp] = state.pc
        state.pc = address.and(0xffff)
    }

    override fun jeq(reg: Int, value: Int) {
        if(state.registers[reg] == value.and(0xff)) {
            state.pc += 2
        }
        state.pc += 2
    }

    override fun jneq(reg: Int, value: Int) {
        if(state.registers[reg] != value.and(0xff)) {
            state.pc += 2
        }
        state.pc += 2
    }

    override fun jeqr(reg1: Int, reg2: Int) {
        if(state.registers[reg1] == state.registers[reg2]) {
            state.pc += 2
        }
        state.pc += 2
    }

    override fun jneqr(reg1: Int, reg2: Int) {
        if(state.registers[reg1] != state.registers[reg2]) {
            state.pc += 2
        }
        state.pc += 2
    }

    override fun set(reg: Int, value: Int) {
        state.registers[reg] = value.and(0xff)
        state.pc += 2
    }

    override fun add(reg: Int, value: Int) {
        state.registers[reg] = (state.registers[reg] + value.and(0xff)).and(0xff)
        state.pc += 2
    }

    override fun setr(reg1: Int, reg2: Int) {
        state.registers[reg1] = state.registers[reg2]
        state.pc += 2
    }

    override fun or(reg1: Int, reg2: Int) {
        state.registers[reg1] = state.registers[reg1].or(state.registers[reg2])
        state.pc += 2
    }

    override fun and(reg1: Int, reg2: Int) {
        state.registers[reg1] = state.registers[reg1].and(state.registers[reg2])
        state.pc += 2
    }

    override fun xor(reg1: Int, reg2: Int) {
        state.registers[reg1] = state.registers[reg1].xor(state.registers[reg2])
        state.pc += 2
    }

    override fun addr(reg1: Int, reg2: Int) {
        val res = state.registers[reg1] + state.registers[reg2]
        state.registers[reg1] = res.and(0xff)
        state.registers[0xf] = if(res > 0xff) 1 else 0
        state.pc += 2
    }

    override fun sub(reg1: Int, reg2: Int) {
        val r1 = state.registers[reg1]
        val r2 = state.registers[reg2]
        state.registers[reg1] = (r1 - r2).and(0xff)
        state.registers[0xf] = if(r1 > r2) 1 else 0
        state.pc += 2
    }

    override fun shr(reg1: Int) {
        state.registers[0xf] = state.registers[reg1].and(0x1)
        state.registers[reg1] = state.registers[reg1].shr(1).and(0xff)
        state.pc += 2
    }

    override fun subb(reg1: Int, reg2: Int) {
        val r1 = state.registers[reg1]
        val r2 = state.registers[reg2]
        state.registers[reg1] = (r2 - r1).and(0xff)
        state.registers[0xf] = if(r2 > r1) 1 else 0
        state.pc += 2
    }

    override fun shl(reg1: Int) {
        state.registers[0xf] = if(state.registers[reg1].and(0x80) != 0) 1 else 0
        state.registers[reg1] = state.registers[reg1].shl(1).and(0xff)
        state.pc += 2
    }

    override fun seti(value: Int) {
        state.index = value.and(0xffff)
        state.pc += 2
    }

    override fun jmpv0(address: Int) {
        state.pc = address.and(0xffff) + state.pc
    }

    override fun rand(reg: Int, value: Int) {
        state.registers[reg] = state.rand.nextInt(0xff + 1).and(value.and(0xff))
        state.pc += 2
    }

    override fun draw(reg1: Int, reg2: Int, value: Int) {
        val locX = state.registers[reg1].toInt().and(0xff)
        val locY = state.registers[reg2].toInt().and(0xff)
        val h = value.and(0xf)

        state.registers[0xf] = 0

        for(y in 0..h-1) {
            val row = state.ram[state.index + y]
            for(x in 0..7) {
                val pixel = row.bit(7-x)
                if(pixel != 0) {
                    val addr = locX + x + (locY + y) * 64
                    if (addr >= 64 * 32) continue
                    val oldPixel = state.vram[addr]
                    val newPixel = oldPixel.toInt().xor(pixel)
                    state.vram[addr] = newPixel.and(0x1).toByte()
                    if (oldPixel.toInt() == 1) {
                        state.registers[0xf] = 1
                    }
                }
            }
        }

        state.vramChanged = true;
        state.pc += 2
    }

    override fun jkey(reg: Int) {
        if(state.keys[state.registers[reg]] != 0) {
            state.pc += 2
        }
        state.pc += 2
    }

    override fun jnkey(reg: Int) {
        if(state.keys[state.registers[reg]] != 0) {
            state.pc += 2
        }
        state.pc += 2
    }

    override fun getdelay(reg: Int) {
        state.registers[reg] = state.delay.and(0xff)
        state.pc += 2
    }

    override fun waitkey(reg: Int) {
        throw UnsupportedOperationException()
    }

    override fun setdelay(reg: Int) {
        state.delay = state.registers[reg]
        state.pc += 2
    }

    override fun setsound(reg: Int) {
        state.sound = state.registers[reg]
        state.pc += 2
    }

    override fun addi(reg: Int) {
        state.index = (state.index + state.registers[reg]).and(0xffff)
        state.pc += 2
    }

    override fun spritei(reg: Int) {
        val idx = state.registers[reg] * 5
        if(idx >= 16 * 5)
            state.index = 0
        else
            state.index = idx
        state.pc += 2
    }

    override fun bcd(reg: Int) {
        throw UnsupportedOperationException()
    }

    override fun push(reg: Int) {
        var addr = state.index
        for(i in 0..reg) {
            state.ram[addr] = state.registers[i].and(0xff).toByte()
            addr += 1
        }
        state.pc += 2
    }

    override fun pop(reg: Int) {
        var addr = state.index
        for(i in 0..reg) {
            state.registers[i] = state.ram[addr].toInt().and(0xff)
            addr += 1
        }
        state.pc += 2
    }
}
