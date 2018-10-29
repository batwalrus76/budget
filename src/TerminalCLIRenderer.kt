class TerminalCLIRenderer {

    companion object {

        fun getTerminalSize(): TerminalDimensions {
            val linesString = System.getenv("LINES")
            val lines: Int = linesString.toInt()
            val columnsString = System.getenv("COLUMNS")
            val columns: Int = columnsString.toInt()
            return TerminalDimensions(lines, columns)
        }
    }
}