package Ex0

import chisel3._
import chisel3.util.Counter

class Control(val rowDim: Int, val colDim: Int) extends Module {

    val io = IO(
        new Bundle {
            val rowMaA = Output(UInt(32.W))
            val colMaA = Output(UInt(32.W))

            val rowMaB = Output(UInt(32.W))
            val colMaB = Output(UInt(32.W))

            val execute = Output(Bool())
            val writeEnable = Output(Bool())
        }
    )
    
    val store_row = RegInit(0.U(32.W))
    val store_col = RegInit(0.U(32.W))

    val calc_col = RegInit(0.U(32.W))
    val calc_rowA = RegInit(0.U(32.W))
    val calc_rowB = RegInit(0.U(32.W))

    val counter = Counter(6*rowDim*colDim)

    // Set default values for all outputs
    io.rowMaA := 0.U
    io.colMaA := 0.U
    io.rowMaB := 0.U
    io.colMaB := 0.U
    io.execute := false.B
    io.writeEnable := false.B

    counter.inc()
    

    when(counter.value < (rowDim*colDim).U) {
        io.writeEnable := true.B

        when(store_col < (colDim-1).U) {
            store_col := store_col + 1.U
        }.elsewhen(store_row < (rowDim-1).U) {
            store_col := 0.U
            store_row := store_row + 1.U 
        }

        io.rowMaA := store_row
        io.colMaA := store_col
        io.rowMaB := store_row
        io.colMaB := store_col

    }.otherwise {
        io.execute := true.B

        when(calc_col < (colDim-1).U) {
            calc_col := calc_col + 1.U
        }.elsewhen(calc_rowB < (rowDim-1).U) {
            calc_col := 0.U
            calc_rowB := calc_rowB + 1.U 
        }.elsewhen(calc_rowA < (rowDim-1).U) {
            calc_col := 0.U
            calc_rowB := 0.U
            calc_rowA := calc_rowA + 1.U
        }

        io.rowMaA := calc_rowA
        io.colMaA := calc_col
        io.rowMaB := calc_rowB
        io.colMaB := calc_col
        
    }
    
}

