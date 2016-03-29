# FXTrivium
Esta aplicación se desarrolla con el objetivo de practicar e implementar el algoritmo de flujo Trivium, la aplicación le ofrese al usuario poder encriptar un texto plano o bien seleccionar un archivo de texto plano para cifrar su contenido, una vez que el suaro proporciona la clave y el vector de incialización, se le da la opción al usuario de escojer como codificar el texto cifrado (la salida), puede seleccionar tres formas:
  - Hexadecimal
  - Base64
  - Binario

Despues de configurar los parametros el usuario puede pulsar el boton de "Encriptar" y el resultado se el mostrara en la pantalla. Tambien se le brinda la opción de guardar en un archivo de texto plano el resultado obtenido.

**Autores**:
  - Guillermo Trejos Sosa
  - Yuliana Leiton Alvarez
  - Steven Rey Sequeira

**Dependencias**:
  1. JRE 8+: Para correr la aplicacion solo se requeire del Java Runtime Enviroment, en caso que se desee compilar se require del JDK 8+
  2. JTrivium: Esta es la libreria que contiene el algortimo, esta libreria se encuntra en este repocitorio dentro del .zip llamado "lib",
     el codigo fuente lo pueden ver este link: https://github.com/stevdrey/JTrivium
  3. FontAwesome: Esta libreria tambien se encuentra en el .zip llamado "lib" o tambien lo pueden descargar del sitio: https://bitbucket.org/Jerady/fontawesomefx 
  
**Estructura**:
  - src: Contiene el codigo fuente de la aplicación, dentro de este directorio podemos encontrar los siguientes archivos:
    - fxtrivium:
      - FXMLDocument.fxml: Contiene la estrcutura en XML de la GUI propiamente
      - FXMLDocumentController.java: Esta clase es el controllador para el archivo mencionado anteriormente, dentro de el se encuentra toda la logica del "negocio", con la que el usuario puede interactuar con la aplicación.
      - DialogUtil.java: Esta es una clase de utilidad para generar cuadros de dialogos, los cuales pueden ser informativos, de adevertencia y para mostrar errores
      - FXTrivium.java: Esta clase contene el metodo "main" el cual es la entrada principal de esta aplicación
      - resource:
        - css:
          - style.css: Esta hoja de estilos contiene estilos basicos para el comportamiento basico de los botones
