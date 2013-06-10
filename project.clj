(defproject citibikes "0.1.0-SNAPSHOT"
  :description "A quick visualization of the availability of Citibikes."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ["-Xmx4g"]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [glgraphics "0.1"]
                 [log4j "1.2.15"]
                 [json4proc "0.1"]
                 [unfolding "0.1"]
                 [quil "1.6.0"]
                 [clj-time "0.4.5"]
                 [org.clojure/data.json "0.2.0"]])
