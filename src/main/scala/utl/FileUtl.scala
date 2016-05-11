package utl

import java.io.{File => JFile}
import java.nio.file.{Files, Path}
import java.util.zip.Inflater

import app.Config._

object FileUtl {

  def checkZlib(body: Array[Byte]) = (!body.isEmpty) && (body(0) == 0x78)

  def decompress(input: Array[Byte]): String = {
    val inflater = new Inflater()
    inflater.setInput(input)

    val decompressedData = new Array[Byte](input.size * 4)

    new String(
      decompressedData.take(
        inflater.inflate(decompressedData))
    )
  }

  def readEntity(path: Path) = {
    val body = Files.readAllBytes(path)
    if (checkZlib(body)) body else Array.empty[Byte]
  }

  def read: Array[Path] = {

    def read(file: JFile): Array[JFile] =
      file.listFiles.flatMap(f => if (f.isDirectory) read(f) else List(f))

    read(new java.io.File(sourceFolder))
      .filter(f => f.getName.takeRight(10) == ".json.zlib")
      .map(f => f.toPath)
      .take(20000)
  }


}
