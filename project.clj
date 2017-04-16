(defproject macchiato/async "0.0.4-SNAPSHOT"
  :description "Async function wrapper for ClojureScript"
  :url "https://github.com/macchiato-framework/macchiato-async"
  :scm {:name "git"
        :url  "https://github.com/macchiato-framework/macchiato-async.git"}
  :license {:name "MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :clojurescript? true
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.495" :scope "provided"]]
  :plugins [[lein-cljsbuild "1.1.4"]
            [codox "0.6.4"]
            [lein-doo "0.1.7"]
            [lein-npm "0.6.2"]]
  :npm {:dependencies [[fibers "1.0.15"]]}
  :profiles {:test
             {:cljsbuild
                   {:builds
                    {:test
                     {:source-paths ["src" "test"]
                      :compiler     {:main          macchiato.test.async.runner
                                     :output-to     "target/test/core.js"
                                     :target        :nodejs
                                     :optimizations :none
                                     :source-map    true
                                     :pretty-print  true}}}}
              :doo {:build "test"}}}
  :aliases
  {"test"
   ["do"
    ["npm" "install"]
    ["clean"]
    ["with-profile" "test" "doo" "node" "once"]]
   "test-watch"
   ["do"
    ["npm" "install"]
    ["clean"]
    ["with-profile" "test" "doo" "node"]]})