(defproject ngramplay "0.1.0-SNAPSHOT"
  :description "Gutenberg word generator"
  :url "n/a"
  :license {:name "WTFPL"
            :url "http://www.wtfpl.net/about/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [expectations "2.0.9"]]
  :plugins [[lein-expectations "0.0.7"]
            [lein-autoexpect "1.4.0"]]
  :main ngramplay.core)
