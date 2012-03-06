(ns river.test.zetta
  (:use clojure.test)

  (:require [river.core :as river]
            [zetta.core :as zetta]
            [zetta.combinators :as zc]
            [zetta.parser.seq :as zp])

  (:use [river.seq :only (produce-seq mapcat* consume)]
        [river.zetta]
        [zetta.combinators :only (sep-by)]
        [zetta.parser.seq :only (number spaces)]))


(def numbers (sep-by number spaces))

(deftest test-parse-consumer
  (let [result (river/run (produce-seq (seq "403 2 75 99 1024"))
                          (parse numbers))]
  (is (= [403 2 75 99 1024] (:result result)))
  (is (= river/eof (:remainder result)))))

(deftest test-parse-consumer-with-small-chunks
  (let [result (river/run (produce-seq 1 (seq "403 2 75 99 1024"))
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

(deftest test-parse*-filter-with-small-chunks
  (let [result (river/run (river/p* (produce-seq 1 (seq "403 2 75 99 1024"))
                                    (parse* numbers)
                                    (mapcat* identity))
                          consume)]
  (is (= [403 2 75 99 1024] (:result result)))
  (is (= river/eof (:remainder result)))))

(deftest test-parse*-filter-with-multiple-producers
  (let [result (river/run (river/p*
                            (river/p* (produce-seq (seq "403 2 75 99 1024"))
                                      (parse* numbers))
                            (mapcat* identity))
                          (river/p*
                            (river/p* (produce-seq (seq "42 43 44"))
                                      (parse* numbers))
                            (mapcat* identity))
                          consume)]
  (is (= [42 43 44 403 2 75 99 1024] (:result result)))
  (is (= river/eof (:remainder result)))))

(deftest test-parse*-filter-with-multiple-producers
  (let [result (river/run (river/p* (produce-seq (seq "403"))
                                    (parse* number))
                          (river/p* (produce-seq (seq "42"))
                                    (parse* number))
                          consume)]
  (is (= [42 403] (:result result)))
  (is (= river/eof (:remainder result)))))

