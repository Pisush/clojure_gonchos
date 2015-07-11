(ns drawing.lines
   (:require [quil.core :as q])
)


(def bg (ref nil))
(def gonch (ref nil))

(defn setup []
  (dosync  
    (ref-set gonch (q/load-image "gonchos.jpg"))
    (ref-set bg (q/load-image "massachusetts.jpg"))
  )
)

(defn draw []
  (q/background-image @bg)
  (q/image @gonch 420 300)
  (q/resize @gonch 290 200)
  (q/rotate  q/QUARTER-PI)
)

(defn rotating-draw [period orig-draw]
  (let [; calculate angle to rotate using handy map-range function
        angle (q/map-range (mod (q/frame-count) period)
                           0 period
                           0 q/TWO-PI)
        center-x (/ (q/width) 2)
        center-h (/ (q/height) 2)]
    ; we want to rotate sketch relative to the center of screen
    ; so we need to move the origin point first and only then rotate
    (q/with-translation [center-x center-y]
      ; rotate screen given angle
      (q/with-rotation [angle]
        ; move origin back to the left top corner (default position)
        ; our middleware should be transparent to the user so we don't
        ; want to change any default settings
        (q/with-translation [(- center-x) (- center-y)]
          ; call user-provided 'draw' function
          (orig-draw))))))

; the middleware function
; it is a regular clojure function
(defn rotate-me [options]
  (let [; user-provided 'draw' or empty function if it's not present
        draw (:draw options (fn []))
        period 200]
    ; replace user-provided draw with
    ; our custom rotating 'draw'
    (assoc options
      :draw (partial rotating-draw period draw))))

(q/defsketch practice
  :title  "hey"
  :size [1000 710]
  :setup setup
  :draw draw
   :middleware [rotate-me])
)