# Scrabble Client

En este fichero encontrarás instrucciones sobre cómo configurar el proyecto ScrabbleClient en Eclipse para su correcto funcionamiento durante ejecución. 

El juego requiere varios parámetros iniciales (todos opcionales y cuyo orden no importa) para poder dar comienzo a la ejecución.

Nota aclaratoria: la notación <...> que se usa a continuación indica el lugar donde debe 
introducirse el valor del parámetro (sin incluir "<" ni ">"), y dentro se indica de manera
esquemática el valor a introducir.

Los argumentos son los siguientes:

1. Nombre del jugador (`-n <nombre>`): nickname asociado al jugador que representa el cliente actual.

2. IP del servidor (`-IP <ip>`): ip identificadora del servidor que se ha lanzado previamente.

3. Puerto (`-p <puerto>`): entero que indica el puerto empleado para conectarse al servidor.

4. Ayuda (`-a`): este parámetro permite obtener una breve descripción de los anteriores parámetros.
   Este parámetro es prioritario sobre el resto, es decir, en caso de ser introducido, solo se
   mostrará el mensaje de ayuda, y la ejecución del programa terminará.

### Observaciones:

- Una entrada incorrecta de los parámetros generarán un comportamiento indeseado de la ejecución.
  Exceptuando al parámetro `-a`, todo el resto deben ir acompañados de su respectivo valor.

- El modo en red del juego Scrabble no permite partidas a través de la consola como interfaz gráfica.

### Ejemplos:

- Los argumentos: `-n Pablo -IP miIp -p 9879` 
  indican que el cliente que ha ejecutado el proyecto tendrá nombre "Pablo", se conectará al
  servidor con identificador "miIp" a través del puerto "9879".