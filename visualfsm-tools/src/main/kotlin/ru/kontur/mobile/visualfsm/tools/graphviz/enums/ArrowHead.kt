package ru.kontur.mobile.visualfsm.tools.graphviz.enums

/**
The enum of types for arrow head with string value for DOT language
 */
enum class ArrowHead(val dotString: String) {
    Normal("normal"),
    ONormal("onormal"),
    Inv("inv"),
    Dot("dot"),
    InvDot("invdot"),
    Odot("odot"),
    InvOdot("invodot"),
    None("none"),
    Empty("empty"),
    Tee("tee"),
    Vee("vee"),
    Diamond("diamond"),
    Box("box"),
    Crow("crow"),
    HalfOpen("halfopen"),
    Curve("curve"),
    ICurve("icurve"),
    DotCurve("dotcurve"),
    ODiamond("odiamond"),
    Open("open"),
    HalfOpenDot("halfopendot"),
    CurveDot("curvedot"),
    TeeDot("teedot"),
    VeeDot("veedot"),
    DiamondDot("diamonddot"),
    BoxDot("boxdot"),
    CrowDot("crowdot"),
    HalfOpenCurve("halfopencurve"),
    CurveODiamond("curveodiamond"),
    CurveOpen("curveopen"),
    InvEmpty("invempty"),
    InvODot("invodot"),
    InvODiamond("invodiamond"),
    InvOpen("invopen"),
    InvCurve("invcurve"),
    InvICurve("invicurve"),
    InvDotCurve("invdotcurve"),
    InvHalfOpen("invhalfopen"),
    InvCurveODiamond("invcurveodiamond"),
    InvCurveOpen("invcurveopen"),
    NoneEmpty("noneempty");
}
