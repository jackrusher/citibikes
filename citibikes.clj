(ns citibikes
  (:import (de.fhpotsdam.unfolding UnfoldingMap)
           (de.fhpotsdam.unfolding.geo Location)
           (de.fhpotsdam.unfolding.providers StamenMapProvider)
           (de.fhpotsdam.unfolding.marker SimplePointMarker))
  (:use quil.core)
  (:require [clojure.data.json :as json]
            [clj-time.core :as ct]
            [clj-time.format :as cf]))

(defn format-time-string
  "Convert the time format in Citybikes JSON to what we'll display."
  [time]
  (-> (cf/unparse (cf/formatter "EEE HH:mm")
                  (cf/parse (cf/formatter "yyyy-MM-dd hh:mm:ss aa") time))
       (.toUpperCase)
       (.replace " " "   ")))

(defn get-rows-from-file
  [filename]
  (let [data (json/read-str (slurp filename))]
    (hash-map
     :time (format-time-string (data "executionTime"))
     :data (->> (second (second data))
                (map #(map (partial get %)
                           ["latitude" "longitude" "totalDocks" "availableBikes" "stationName"]))
                (remove #(= 0 (nth % 2)))))))

(def station-data
  (let [dir "data"]
    (doall (map #(get-rows-from-file (str dir "/" (.getName %)))
                (remove #(= 0 (.length %)) (.listFiles (java.io.File. dir)))))))

(def trips-by-tick ;; rough estimate of the number of trips/data file
  (map (fn [pair]
         (int (/ (apply + (map #(Math/abs (- %1 %2)) (first pair) (second pair))) 2)))
       (partition 2 1
                  (for [stations station-data]
                    (for [station (:data stations)] (nth station 3))))))

(defn setup []
  (set-state!
   :stations   (atom station-data)
   :trips      (atom trips-by-tick)
   :trip-count (atom 0)
   :map (doto (UnfoldingMap.
               (quil.applet/current-applet)
               (de.fhpotsdam.unfolding.providers.StamenMapProvider$TonerBackground.))
          (.zoomToLevel 13)
          (.panTo (Location. 40.725913511499996 -73.98672378))
          (.draw)))
  (text-font (create-font "OCR A Std" 22)) ; TODO set this to a typeface you have
  (smooth)
  (no-stroke)
  (frame-rate 4))

(defn draw []
  (background 0)
  (no-stroke)
  (translate 30 3 240)
  (let [stations   (first @(state :stations))
        the-map    (state :map)
        trip-count @(state :trip-count)]
    (.draw the-map)
    (doseq [station (stations :data)]
      (let [[latitude longitude capacity availability] station
            position (.getScreenPosition (SimplePointMarker. (Location. latitude longitude)) the-map)
            x (.x position)
            y (.y position)
            scaled-capacity (/ capacity 1.6)
            scaled-availability (/ availability 1.6)]
        (fill 67 211 227 100)
        (ellipse x y scaled-capacity scaled-capacity)
        (fill 33 105 103 200)
        (ellipse x y scaled-availability scaled-availability)))
    (fill 255 94 170 230)
    (rect 70 138 180 54)
    (fill 255 255 255)
    (text (stations :time) 72 160)
    (text (str "TRIPS " trip-count) 72 186)
;;    (save-frame) ; uncomment to save every frame as tif
    (when-let [next-trips (first @(state :trips))]
      (reset! (state :trip-count) (+ trip-count next-trips)))
    (reset! (state :trips) (rest @(state :trips)))
    (reset! (state :stations) (rest @(state :stations)))
    (when (empty? @(state :stations)) (no-loop))))

;; this will start the sketch
(defsketch example
  :title "Citibike locations"
  :setup setup
  :draw draw
  :renderer :opengl
  :size [700 1000])

(sketch-stop example)
