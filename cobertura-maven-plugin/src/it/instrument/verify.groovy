/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Lukasz Kusek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
XmlParser parser = new XmlParser()
def coverage = parser.parse(new File(basedir, "target/cobertura/coverage.xml"))
println("coverage.xml content: " + coverage)

def classes = coverage.packages.package[0].classes
def ToCover1 = classes.'*'.find { clazz -> clazz.@name == "ToCover1"}
def ToCover2 = classes.'*'.find { clazz -> clazz.@name == "ToCover2"}
println("ToCover1 class coverage: " + ToCover1)
println("ToCover2 class coverage: " + ToCover2)

ToCover1.@"line-rate" == "1.0" &&
        ToCover1.@"branch-rate" == "1.0" &&
        ToCover2.@"line-rate" == "0.75" &&
        ToCover2.@"branch-rate" == "0.5"
