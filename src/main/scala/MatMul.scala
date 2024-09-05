package Ex0

import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule

class MatMul(val rowDimsA: Int, val colDimsA: Int) extends MultiIOModule {

  val io = IO(
    new Bundle {
      val dataInA     = Input(UInt(32.W))
      val dataInB     = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))
      val outputValid = Output(Bool())
    }
  )

  val debug = IO(
    new Bundle {
      val debug_writeEn = Output(Bool())
      val debug_execute = Output(Bool())
      val debug_dotProdValid = Output(Bool())
      val debug_rowA = Output(UInt(32.W))
      val debug_colA = Output(UInt(32.W))
      val debug_rowB = Output(UInt(32.W))
      val debug_colB = Output(UInt(32.W))
      val debug_MaAout = Output(UInt(32.W))
      val debug_MaBout = Output(UInt(32.W))

    }
  )


  /**
    * Your code here
    */
  val matrixA     = Module(new Matrix(rowDimsA, colDimsA)).io
  val matrixB     = Module(new Matrix(rowDimsA, colDimsA)).io
  val dotProdCalc = Module(new DotProd(colDimsA)).io
  val control     = Module(new Control(rowDimsA, colDimsA)).io
  
  matrixA.writeEnable := control.writeEnable
  matrixB.writeEnable := control.writeEnable

  matrixA.rowIdx := control.rowMaA
  matrixA.colIdx := control.colMaA
  matrixB.rowIdx := control.rowMaB
  matrixB.colIdx := control.colMaB

  matrixA.dataIn := io.dataInA
  matrixB.dataIn := io.dataInB

  dotProdCalc.dataInA := matrixA.dataOut
  dotProdCalc.dataInB := matrixB.dataOut

  when(control.execute){
    io.outputValid := dotProdCalc.outputValid
  }.otherwise{
    io.outputValid := false.B
  }

  io.dataOut := dotProdCalc.dataOut

  // Debug signals
  debug.debug_writeEn := control.writeEnable
  debug.debug_execute := control.execute
  debug.debug_dotProdValid := dotProdCalc.outputValid
  debug.debug_rowA := control.rowMaA
  debug.debug_colA := control.colMaA
  debug.debug_rowB := control.rowMaB
  debug.debug_colB := control.colMaB
  debug.debug_MaAout := matrixA.dataOut
  debug.debug_MaBout := matrixB.dataOut
  
}
