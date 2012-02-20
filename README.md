# zetta-river

zetta-river is a bridge that allows the developer to use zetta-parser parser
combinators to transform streams from river's producers.

This library provides one consumer and one filter:

* `parse` is a consumer that will use the stream from a river producer
  and return the result of the given zetta parser.

* `parse*` is a filter that will transform the stream of a river producer
  into a seq of results from the given zetta parser.

## Usage

Once you know how to use [river]{http://github.com/roman/river} and
the [zetta-parser]{http://github.com/roman/zetta-parser}, it is really
easy to get started. An example:

```clojure
(ns numbers
  (:require [river.core :as river]
            [zetta.core :as zetta])

  (:use [river.seq
        :only
          [produce-seq mapcat* consume]]

        [river.io
        :only
          [produce-reader-chars]]

        [zetta.combinators
        :only
          [sep-by]]

        [zetta.parser.seq
        :only
          [number spaces]]

        [zetta.river]))

(def number-between-spaces (sep-by number spaces))

; This will return a list of parsed numbers
(println (river/run (produce-seq "34   57  99  130   45")
                      (parse number-between-spaces)))

; This will return the same list, but using a filter
; and the river.seq/consume consumer
(println (river/run (produce-seq "34   57  99  130   45"
                      (parse* number-between-spaces
                        (mapcat* id
                          consume)))))

; Say for example you want to use the same consumer on a File
; instead of string in the source code
;(println (river/run (produce-reader-chars "path/to/input.txt"
;                      (parse* number-between-spaces
;                        (mapcat* id
;                          consume)))))

```

## License

Copyright (C) 2012 Roman Gonzalez

Distributed under the Eclipse Public License, the same as Clojure.
