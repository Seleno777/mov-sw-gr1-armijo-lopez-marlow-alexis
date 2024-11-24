import java.util.Date

fun main(args: Array<String>) {

    // Variables inmutables no se reasignan
    val inmutable: String = "Marlow"

    // Variables mutables aquellas que se reasignan
    var mutable: String = "Alexis"
    mutable = "Marlow"

    // Duck typing
    val ejemploVariable = "Marlow Armijo".trim()
    val edadEjemplo: Int = 12

    // Variables primitivas
    val nombreProfesor: String = "Marlow Armijo"
    val sueldo: Double = 1.2
    val estadoCivil: Char = 'C'
    val mayorEdad: Boolean = true

    val fechaNacimiento: Date = Date()

    // WHEN --> SWITCH
    val estadoCivilWhen = "C"

    when (estadoCivilWhen) {
        "C" -> {
            println("Casado")
        }
        "S" -> {
            println("Soltero")
        }
        else -> {
            println("No sabemos")
        }
    }

    val esSoltero = (estadoCivilWhen == "S")
    val coqueteo = if (esSoltero) "Si" else "No"

    imprimirNombre("Marlow Armijo")

    //Parte de la funcion calcular sueldo

    calcularSueldo(10.00)
    calcularSueldo(10.00,15.00,20.00)

    //named parameters

    calcularSueldo(10.00, bonoEspecial = 20.00)
    calcularSueldo(bonoEspecial = 20.00, sueldo = 10.00, tasa = 14.00)

    val sumaA= Suma(1,1)
    val sumaB= Suma(null,1)
    val sumaC= Suma(1,null)
    val sumaD= Suma(null,null)

    sumaA.sumar()
    sumaB.sumar()
    sumaC.sumar()
    sumaD.sumar()

    println(Suma.pi)
    println(Suma.elevaralcuadrado(2))
    println(Suma.historialSumas)

    val arregloEstatico: Array<Int> = arrayOf<Int>(1,2,3)
    println(arregloEstatico)

    val arregloDInamico: Array<Int> = arrayOf<Int>(1,2,3,4,5,6,7,8,9,10)

    println(arregloDInamico)
    arregloDInamico.add(11)
    arregloDInamico.add(12)
    println(arregloDInamico)
/// FOREACH

    val respuestaForEach: Unit= arregloDInamico
        .forEach { valorActual: Int -> println(valorActual) }

    arregloDInamico.forEach { println("valor actual (it): ${it}") }

///MAP
    val respuestMap: List<Double> =arregloDinamico
        .map { valorActual:Int ->
            return@map valorActual.toDouble()+100
        }

    println(respuestMap)
    val respuestaMapDos= arregloDInamico.map { it+15 }
    println(respuestaMapDos)

    //OPERADOR FILTER

    val respuestaFilter: List<Int> = arregloDinamico
        .filter {valorActual :Int ->
            val mayorACinco: Boolean= valorActual > 5
            return@filter mayorACinco
        }

    val respuestaFilterDos = arregloDInamico.filter{ it<= 5}
    println(respuestaFilter)
    println(respuestaFilterDos)

// OR AND
    //OR --> ANY (ALGUNO CUMPLE)
    //AND -->ALL (TODOS CUMPLEN)
    val respuestaAny:Boolean= arregloDInamico
        .any { valorActual: Int ->
            return@any (valorActual > 5)
        }
    println(respuestaAny)

    val respuestaAll: Boolean = arregloDInamico
        .all { valorActual: Int ->
            return@all (valorActual > 5)
        }

    println(respuestaAll)

}




fun imprimirNombre(nombre: String) {
    fun otraFuncionAdentro() {
        println("Otra función adentro")
    }

    println("Nombre: $nombre") // Uso sin llaves
    println("Nombre: ${nombre}") // Uso con llaves opcional
    println("Nombre: ${nombre + nombre}") // Uso con llaves (concatenación)
    println("Nombre: ${nombre.uppercase()}") // Uso con llaves (función)
    println("Nombre: $nombre.uppercase()")

    otraFuncionAdentro()
}

fun calcularSueldo(

    sueldo: Double,
    tasa: Double=12.00,
    bonoEspecial: Double?=null,

): Double {

    if(bonoEspecial == null) {
        return sueldo*(100/tasa)
    }else{
        return sueldo*(100/tasa)* bonoEspecial
    }


}

abstract class NumerosJava{

    protected val  numeroUno: Int
    private val  numeroDos:Int

    constructor(
        uno:Int,
        dos:Int
    ){
            this.numeroUno = uno
            this.numeroDos = dos
    }
}

abstract class Numeros(

    protected  val numeroUno: Int,
    protected  val numeroDos: Int,
    parametroNoUsadoNoPropiedadDeLaClase: Int?= null

){

    init{
        this.numeroUno
        this.numeroDos
        println("inicializando")
    }
}

class Suma(
    unoParamtro: Int,
    dosParamtro: Int,

):Numeros(unoParamtro, dosParamtro){

    public val soyPublicoExplicito: String="Publicas"
    val soyPublicoImplicitamente: String="Implicitamente"

    init{
        this.numeroUno
        this.numeroDos
        numeroUno
        numeroDos
        this.soyPublicoImplicitamente
        soyPublicoExplicito
    }
    constructor(
        uno: Int?,
        dos: Int,
    ): this(
        if(uno==null) 0 else uno,
        dos
    ){
        ///Bloque de codigo del constructor secundario
    }

    constructor(// CONSTRUCTOR SECUNDARIO
        uno: Int,
        dos: Int?,
      ):this (
          uno,
          if(dos==null) 0 else dos
      )

    constructor( // COSNTRUCTOR SECUNDARIO
        uno: Int?,
        dos: Int?,
    ):this(
        if(uno==null) 0 else uno,
        if(dos==null) 0 else dos
    )


    ///DECLARAR METODOS

    fun sumar():Int
    {
        val total= numeroUno+ numeroDos
        agregarHistorial(total)
        return total
    }

    companion object{


        val pi=3.14

        fun elevaralcuadrado(num:Int):Int{ return num*num}
        val historialSumas= arrayListOf<Int>()

        fun agregarHistorial(total: Int)
        {
            historialSumas.add(total)
        }
    }





}