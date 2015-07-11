(ns muse.runner
  (:require [cljs.test :as test]
            [muse.test.cats]))

(enable-console-print!)

(defn main
  []
  (test/run-tests (test/empty-env)
                  'muse.test.cats))

(set! *main-cli-fn* main)
