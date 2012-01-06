(ns zetta.river

  ^{
    :author "Roman Gonzalez"
  }

  (:require [river.core :as river]
            [zetta.core :as zetta]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Utility functions
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- finish-parser
  "Ensures that the parser has finished, once it is finished it is
  returned."
  [parser]
  (if (zetta/partial? parser)
    (parser "")
    parser))


(defn- get-parser-result
  "Returns the result and the remainder value of the parser, when
  the parser is a failure returns nil."
  [parser]
  (cond
    (zetta/failure? parser) [nil (:remainder parser)]
    (zetta/done? parser)    [(:result parser) (:remainder parser)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Consumers
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse
  "Consumer that receives a zetta parser and returns the parsed
  result. In case the parser fails to match, it will return a nil
  value."
  [parser0]
  (let [
    consumer (fn consumer-fn [parser stream]
               (cond
                 (river/eof? stream)
                   (let [
                     [result _] (get-parser-result
                                    (finish-parser parser))]

                   (river/yield result river/eof))

                 (zetta/partial? parser)
                   (river/continue #(consumer-fn (parser stream) %))

                 :else
                   (let [[result remainder] (get-parser-result parser)]
                   (river/yield result
                                (concat  remainder
                                         stream)))))]
    #(consumer (partial zetta/parse parser0) %)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Filters
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse*
  "Filter that transforms a given input stream into results of the given
  zetta-parser, this will use the parser consumer internally."
  [parser inner-consumer]
  (river/to-filter (parse parser) inner-consumer))


