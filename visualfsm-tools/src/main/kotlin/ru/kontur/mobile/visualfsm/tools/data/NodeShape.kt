package ru.kontur.mobile.visualfsm.tools.data

/**
The enum of node shapes with string value for DOT language
 */
enum class NodeShape(val dotString: String) {
    Box("box"),
    Polygon("polygon"),
    Ellipse("ellipse"),
    Oval("oval"),
    Circle("circle"),
    Point("point"),
    Egg("egg"),
    Triangle("triangle"),
    Plaintext("plaintext"),
    Diamond("diamond"),
    Trapezium("trapezium"),
    Parallelogram("parallelogram"),
    House("house"),
    Pentagon("pentagon"),
    Hexagon("hexagon"),
    Septagon("septagon"),
    Octagon("octagon"),
    DoubleCircle("doublecircle"),
    DoubleOctagon("doubleoctagon"),
    TripleOctagon("tripleoctagon"),
    InvTriangle("invtriangle"),
    InvTrapezium("invtrapezium"),
    InvHouse("invhouse"),
    Mdiamond("Mdiamond"),
    Msquare("Msquare"),
    Mcircle("Mcircle");
}