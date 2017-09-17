/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wbq.common.util.csv

import com.univocity.parsers.csv.{CsvParser, CsvParserSettings, UnescapedQuoteHandling}

//Quote from org.apache.spark.sql.execution.datasources.csv.CsvReader
object CSVParser {
  private val parser: CsvParser = {
    val settings = new CsvParserSettings()
//    val format = settings.getFormat
//    format.setDelimiter(params.delimiter)
//    format.setQuote(params.quote)
//    format.setQuoteEscape(params.escape)
//    format.setComment(params.comment)
//    settings.setIgnoreLeadingWhitespaces(params.ignoreLeadingWhiteSpaceFlag)
//    settings.setIgnoreTrailingWhitespaces(params.ignoreTrailingWhiteSpaceFlag)
    settings.setReadInputOnSeparateThread(false)
//    settings.setInputBufferSize(params.inputBufferSize)
//    settings.setMaxColumns(params.maxColumns)
//    settings.setNullValue(params.nullValue)
//    settings.setMaxCharsPerColumn(params.maxCharsPerColumn)
    settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.STOP_AT_DELIMITER)

    new CsvParser(settings)
  }

  def parseLine(line: String): Array[String] = parser.parseLine(line)
}
