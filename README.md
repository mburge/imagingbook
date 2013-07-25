Digital Image Processing
===========
Welcome to this web site accompanying our textbooks on digital image processing. Our books provide a modern, algorithmic introduction to digital image processing, designed to be used both by learners looking for a firm foundation on which to build and practitioners in search of critical analysis and modern implementations of the most important techniques. While we concentrate on practical applications and working implementations, we do so without glossing over the important formal details and mathematics necessary for a deeper understanding of the algorithms. In preparing these texts, we started from the premise that simply creating a recipe book of imaging solutions would not provide the deeper understanding needed to apply these techniques to novel problems. Instead, our solutions typically develop stepwise along three different perspectives: (a) in mathematical form, (b) as portable pseudocode algorithms, and (c) as complete implementations in a real programming language Java. We use a common and consistent notation throughout to intertwine all three perspectives, thus providing multiple but linked views of problems and solutions. For our readers this site provides the following documents and materials: the complete [source code](http://imagingbook.com/source/), [errata](http://imagingbook.com/errata/), [images and slide materials](http://imagingbook.com/materials/), an [ImageJ reference and tutorial](http://imagingbook.com/imagej-tutorial/), and useful [links to other resources](http://imagingbook.com/links/).

Our books are currently available in the following print and online editions: 
## English Editions

* [Digital Image Processing – An Algorithmic Introduction using Java](http://imagingbook.wordpress.com/books/english-edition-hardcover/) Springer-Verlag, New York (2008). ISBN 978-1-84628-968-2. Single-volume hardcover edition with 566 pages and 394 illustrations, full color print. [Read more..](http://imagingbook.wordpress.com/books/english-edition-hardcover/)
* [Principles of Digital Image Processing (Vol. 1-3)](http://imagingbook.wordpress.com/books/englisch-edition-3-vol-softcover/) Springer Undergraduate Topics in Computer Science, New York (2009-2013). 3-volume softbound edition, full color print.  Volumes 1-2 cover essentially the same contents as the hardcover edition. Volume 3 (added in 2013) covers all-new content. [Read more..](http://imagingbook.wordpress.com/books/englisch-edition-3-vol-softcover/)
	* Vol. 1: [Fundamental Techniques ](http://imagingbook.wordpress.com/books/englisch-edition-3-vol-softcover/)(ISBN 978-1-84800-191-6) 
	* Vol. 2: [Core Algorithms ](http://imagingbook.wordpress.com/books/englisch-edition-3-vol-softcover/)(ISBN 978-1-84800-195-4) 
	* Vol. 3: [Advanced Methods ](http://imagingbook.wordpress.com/books/englisch-edition-3-vol-softcover/)(ISBN 978-1-84882-918-3) This is currently the most recent English edition. 
## German Editions
   * [ Digitale Bildverarbeitung – Eine Einführung mit Java und ImageJ](http://imagingbook.wordpress.com/books/english-edition-hardcover/) Springer-Verlag Heidelberg, 2nd edition (2006). Single-volume hardcover edition. ISBN 978-3-540-30941-3. [Read more..](http://imagingbook.wordpress.com/books/english-edition-hardcover/)
   * Digitale Bildverarbeitung – Eine algorithmische Einführung mit Java (to appear 2014) Springer-Verlag Heidelberg, 3rd edition (to be published in Spring 2014). Single-volume hardcover edition. ISBN 978-3-642-04603-2. This completely revisedGerman edition will include all materials previously only published in English plus additional chapters on selected new topics.
## Chinese Edition
   * Digital Image Processing – An Algorithmic Introduction using Java Springer / Tsinghua University Press (2010). ISBN 978-7-302-21365-9. [Read more..](http://imagingbook.wordpress.com/books/chinese-edition/)
# Source Code
The algorithms in our books are implemented in Java and [ImageJ ](http://rsbweb.nih.gov/ij/index.html) which is a small, simple and flexible environment for digital image processing, originally conceived (and still being developed) by Wayne Rasband at the U.S. National Institutes of Health (NIH). Our source code is distributed in two parts: 

  1. A single archive file that contains the common source code and compiled Java files shared by all book editions: `imagingbook.jar`
  2. A dedicated ZIP archive `BurgerBurge..-plugins.zip` for each book edition that contains a set of ImageJ plugins, structured by individual book chapters.
## Downloads
1. Common archive (required for all): [imagingbook.jar](https://dl.dropbox.com/s/03by3ctfwf9k6fw/imagingbook.jar)
2. Select the dedicated plugin set for your book edition: 
	* Professional Edition: [download](https://dl.dropbox.com/s/ub5rh30wwxjs58p/BurgerBurgeEn1-plugins.zip)
	* Undergraduate Series (Vol. 1-3): [download](https://dl.dropbox.com/s/z6utfwsm8vv4trs/BurgerBurgeUtics123-plugins.zip)
	* German Edition: [download](https://dl.dropbox.com/s/xdu3p50sbr3vqf1/BurgerBurgeDe1-plugins.zip)
	* Chinese edition: _same as Professional Edition_
3. Additional JAR files required: 
     [commons-math3-3.1.1.jar](https://dl.dropbox.com/s/nf4yy5wjc3orl06/commons-math3-3.1.1.jar)
     [Jama-1.0.2.jar](https://dl.dropbox.com/s/8ihpb84ox97r1k9/Jama-1.0.2.jar)
## Installation and setup
It is assumed that you have an up-to-date ImageJ installation available. If not, first set up a minimal ImageJ environment (following the [instructions on the ImageJ website](http://rsbweb.nih.gov/ij/download.html)), where <ImageJ> denotes ImageJ's root folder. 

  1. Place imagingbook.jar in the <IJ>/plugins/jars folder (create this folder if it does not already exist).
  2. Expand the zip file with the dedicated plugin set inside the  <IJ>/plugins folder. Each book chapter is represented by a sub-folder in plugins.
  3. Place the additonal JAR files in <IJ>/plugins/jars.
  4. Start ImageJ, chapters and contained plugins should now be visible in the 'Plugins' menu. Use 'Compile and run..' to execute plugins after any modification.
Note that this source code is being continuously updated, enhanced and adapted to newer releases of Java and ImageJ. Thus some of the original code examples in the printed texts may have been replaced by newer and improved versions without notice, although we attempt to preserve the original functionality as much as possible. This may affect class structures, method parameters and coding practices in general. The API thus cannot be guaranteed to be stable and may be incompatible with older code. 

## Licensing information
This software is provided as a supplement to the authors' textbooks on digital image processing published by Springer-Verlag in various languages and editions. Permission to use and distribute this software is granted under the [BSD 2-Clause "Simplified" License](http://opensource.org/licenses/BSD-2-Clause) and below. Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. All rights reserved. 
    
```
The BSD 2-Clause License
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the above copyright notice,  this list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the  above  copyright  notice, this list of conditions and the following disclaimer  in  the  documentation and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,  THE  IMPLIED WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR A  PARTICULAR  PURPOSE   ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLUDING, BUT NOT LIMITED TO,  PROCUREMENT OF SUBSTITUTE GOODS  OR  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR  BUSINESS INTERRUPTION)  HOWEVER  CAUSED  AND ON ANY THEORY OF LIABILITY, WHETHER IN  CONTRACT,  STRICT  LIABILITY,  OR  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE  OF  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the authors and should not be interpreted as representing official policies, either expressed or implied, of the FreeBSD Project.
```

# Errata
Unfortunately mistakes happen and we are grateful to our readers for reporting them and studying the texts so carefully! Download the set of replacement pages for each book edition below. All corrections and changes are highlighted in color. 

* Professional Edition [[PDF](http://imagingbook.files.wordpress.com/2013/06/burgerburge-en1-errata.pdf)]
* Undergraduate Series
* Vol. 1: Fundamental Techniques [[PDF](http://imagingbook.files.wordpress.com/2013/06/burgerburge-utics1-errata.pdf)]
* Vol. 2: Core Algorithms [[PDF](http://imagingbook.files.wordpress.com/2013/06/burgerburge-utics2-errata.pdf)]
* Vol. 3: Advanced Methods [none so far]
* German Edition [[PDF](http://imagingbook.files.wordpress.com/2013/06/burgerburge-de2-errata.pdf)]
