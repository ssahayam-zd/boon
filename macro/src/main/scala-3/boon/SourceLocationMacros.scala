package boon

/*
 * Copyright (c) 2014-2019 by The Minitest Project Developers.
 * Some rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//Copied from https://github.com/monix/minitest/blob/master/shared/src/main/scala-3/minitest/macros/SourceLocationMacros.scala

import scala.quoted._

object SourceLocationMacros {
  def impl()(using Quotes): Expr[SourceLocation] = {
    import quotes.reflect._
    val pos = Position.ofMacroExpansion
    val path = pos.sourceFile.path
    val fileName = pos.sourceFile.name
    val startLine = pos.startLine + 1
    '{
      SourceLocation(
        ${Expr(Some(path))},
        ${Expr(Some(fileName))},
        ${Expr(startLine)}
      )
    }
  }
}

trait SourceLocationMacros {
  inline implicit def fromContext: SourceLocation =
    ${ SourceLocationMacros.impl() }
}