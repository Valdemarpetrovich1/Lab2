(ns Lab2.core
(:require [clojure.core.async :refer [chan >! <! go go-loop alts!]]))

(def channelOfNumbers (chan 10))


(defn fillSourceChannel [start end]
  (dotimes [i (- end start)]
    (go (>! channelOfNumbers (+ start i)))))


(fillSourceChannel 1 20)

(defn sortNumbersFromChannel [chanOfNumbers number]
  (let [remainder-channels (into {} (for [r (range number)]
                                   [r (chan 10)]))]
    (go-loop []
      (when-let [num (<! chanOfNumbers)]
        (let [r (rem num number)
              channel (get remainder-channels r)]
          (>! channel num))
        (recur)))
    remainder-channels))

(let [output-channels (sortNumbersFromChannel channelOfNumbers 3)]

  (Thread/sleep 1000)
  (doseq [[r channel] output-channels]
    (println (str "Канал с остатком от деления " r ":"))   
    (go-loop []
      (when-let [num (<! channel)]
        (print num " ")
        (recur)))
    (Thread/sleep 100)
    (println)))
    (Thread/sleep 1000)

