package com.soberg.aoc.utlities.datastructures

/** A uniform 2D grid with elements of type [T]. */
data class Grid2D<T>(
    private val grid: List<List<T>>,
) {
    init {
        val colSize = grid[0].size
        for (i in 1..<grid.size) {
            if (colSize != grid[i].size) {
                error("Grid is not uniform - row at index $i has a differing # of columns")
            }
        }
    }

    val rowSize: Int
        get() = grid.size

    val colSize: Int
        get() = grid[0].size

    infix operator fun get(location: Location): T = grid[location.row][location.col]

    operator fun get(row: Int, col: Int): T = grid[row][col]

    /** @return true if the specified [location] is in the bounds of this grid, false if not. */
    fun isInBounds(location: Location): Boolean = isInBounds(location.row, location.col)

    /** @return true if the specified [row], [col] location is in the bounds of this grid, false if not. */
    fun isInBounds(row: Int, col: Int): Boolean =
        (row <= grid.lastIndex && row >= 0) &&
                (col <= grid[0].lastIndex && col >= 0)

    /** @return The element for the specified element starting at [from] and moving in [direction] for
     *  [distance], null if this element doesn't exist or location would be out of bounds of the grid. */
    fun elementAt(
        from: Location,
        direction: Direction,
        distance: Int,
    ): T? {
        val endLocation = from.move(direction, distance)
        return if (isInBounds(endLocation)) {
            get(endLocation)
        } else null
    }

    /** @return The collection of elements (as a [List]) that exist from the start [from] (inclusive)
     * in [direction] for [numElementsToCollect], null if any location would be out of bounds of this grid.
     *
     * Note that [numElementsToCollect] is inclusive of the starting location - therefore, a value
     * of 1 would result in a single item list of just the element at [from]. */
    fun collect(
        from: Location,
        direction: Direction,
        numElementsToCollect: Int,
    ) : List<T>? {
        val finalLocation = from.move(direction, numElementsToCollect - 1)
        if (!isInBounds(finalLocation)) {
            return null
        }
        return buildList {
            for (i in 0..<numElementsToCollect) {
                add(get(from.move(direction, i)))
            }
        }
    }

    /** Walks through each location in this grid. */
    inline fun traverse(at: (location: Location) -> Unit) {
        for (row in 0..<rowSize) {
            for (col in 0..<colSize) {
                at(Location(row, col))
            }
        }
    }

    /** @return A new copy of this [Grid2D] with the specified [element] replaced at [location].
     * @throws [IllegalArgumentException] if [location] not in bounds for this grid. */
    fun replace(location: Location, element: T): Grid2D<T> {
        require(isInBounds(location)) {
            "$location not in bounds for this grid"
        }
        val replacedRow: MutableList<T> = grid[location.row].toMutableList()
        replacedRow[location.col] = element
        return copy(
            grid = grid.toMutableList().apply {
                set(location.row, replacedRow)
            }
        )
    }

    data class Location(
        val row: Int,
        val col: Int,
    ) {
        fun move(direction: Direction, distance: Int = 1) = Location(
            row = row + (direction.row.integerMovement * distance),
            col = col + (direction.col.integerMovement * distance),
        )

        override fun toString(): String = "{Row: $row, Col: $col}"
    }

    enum class Direction(
        internal val row: Movement = Movement.None,
        internal val col: Movement = Movement.None,
    ) {
        North(row = Movement.Negative),
        NorthEast(row = Movement.Negative, col = Movement.Positive),
        East(col = Movement.Positive),
        SouthEast(row = Movement.Positive, col = Movement.Positive),
        South(row = Movement.Positive),
        SouthWest(row = Movement.Positive, col = Movement.Negative),
        West(col = Movement.Negative),
        NorthWest(row = Movement.Negative, col = Movement.Negative);

        internal enum class Movement(
            val integerMovement: Int,
        ) {
            Positive(1),
            Negative(-1),
            None(0),
        }
    }
}

fun <T> List<List<T>>.toGrid2D() = Grid2D(this)

/** Converts the common AoC input (List of lines (as String) from input file). */
inline fun <T> List<String>.toGrid2D(convertLine: (String) -> List<T>) = map { line ->
    convertLine(line)
}.toGrid2D()
