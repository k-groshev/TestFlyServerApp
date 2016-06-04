package utl

import java.io._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import actors.Statistics

object CSVWriter {

  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")

  val separator = "\t"
  val endLine = "\n"

  val encoding = "Cp1251"

  def printStatisticsToFile(stat: Statistics) = {

    val path = "stats-data"
    (new File(path)).mkdirs()

    val name = s"statistics_${LocalDateTime.now.format(formatter)}"

    val file = new File(s"$path//$name")

    val writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(file), encoding))

    try {
      stat.results.foreach(
        r => writer.append(s"${r.avgTime}$separator${r.count}$separator${r.countParseError}$endLine"))
    } finally {
      writer.flush()
      writer.close()
    }

  }
}
