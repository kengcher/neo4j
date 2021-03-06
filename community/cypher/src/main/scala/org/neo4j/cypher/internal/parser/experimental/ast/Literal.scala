/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.parser.experimental.ast

import org.neo4j.cypher.internal.parser.experimental._
import org.neo4j.cypher.internal.symbols._
import org.neo4j.cypher.internal.commands.{expressions => commandexpressions}

sealed trait Number extends Expression {
  def value : java.lang.Number

  def toCommand = commandexpressions.Literal(value)
}

case class SignedInteger(value: java.lang.Long, token: InputToken) extends Number with SimpleTypedExpression {
  protected def possibleTypes = Set(LongType())
}
case class UnsignedInteger(value: java.lang.Long, token: InputToken) extends Number with SimpleTypedExpression {
  protected def possibleTypes = Set(LongType())
  if (value < 0)
    throw new IllegalArgumentException("signed integer used in UnsignedInteger Literal")
}

case class Double(value: java.lang.Double, token: InputToken) extends Number with SimpleTypedExpression {
  protected def possibleTypes = Set(DoubleType())
}

case class StringLiteral(value: String, token: InputToken) extends Expression with SimpleTypedExpression {
  protected def possibleTypes = Set(StringType())

  def toCommand = commandexpressions.Literal(value)
}

case class Range(lower: Option[UnsignedInteger], upper: Option[UnsignedInteger], token: InputToken) extends AstNode
