(ns zetta.test.river
  (:use clojure.test)

  (:require [river.core :as river]
            [zetta.core :as zetta]
            [zetta.combinators :as zc]
            [zetta.parser.seq :as zp])

  (:use [river.seq :only (produce-seq mapcat* consume)]
        [zetta.combinators :only (sep-by)]
        [zetta.parser.seq :only (number spaces)]
        [zetta.river]))


(def numbers (sep-by number spaces))

(deftest test-parse-consumer
  (let [result (river/run (produce-seq (seq "403 2 75 99 1024"))
                          (parse numbers))]
  (is (= [403 2 75 99 1024] (:result result)))
  (is (= river/eof (:remainder result)))))


(deftest test-parse*-filter
  (let [result (river/run (river/p* (produce-seq (seq "403 2 75 99 1024"))
                                    (parse* numbers)
                                    (mapcat* identity))
                          consume)]
  (is (= [403 2 75 99 1024] (:result result)))
  (is (= river/eof (:remainder result)))))
