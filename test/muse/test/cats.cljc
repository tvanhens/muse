(ns muse.test.cats
  #?@(:cljs
      [(:require-macros [cljs.core.async.macros :refer [go]])

       (:require [cljs.test :as t :refer-macros [async is]]
                 [muse.core :as muse :refer-macros [run!]]
                 [cljs.core.async :refer [<!]]
                 [cats.core :as m])])

  #?(:clj
     (:require [clojure.test :as t]
               [muse.core :as muse]
               [clojure.core.async :refer [go <! <!!]]
               [cats.core :as m]))

  (:refer-clojure :exclude [run!]))

;; Had to append Data since cljs has ->List in core
(defrecord ListData [size]
  muse/DataSource
  (fetch [_] (go (range size)))
  muse/LabeledSource
  (resource-id [_] size))

(defrecord SingleData [seed]
  muse/DataSource
  (fetch [_] (go seed))
  muse/LabeledSource
  (resource-id [_] seed))

(t/deftest cats-api
  (t/is (satisfies? muse/MuseAST (m/fmap count (muse/value (range 10)))))
  (t/is (satisfies? muse/MuseAST
                    (m/with-monad muse/ast-monad
                      (m/fmap count (ListData. 10)))))
  (t/is (satisfies? muse/MuseAST
                    (m/with-monad muse/ast-monad
                      (m/bind (SingleData. 10) (fn [num] (SingleData. (inc num))))))))

#?(:clj
   (t/deftest runner-macros
     (t/is (= 5 (<!! (muse/run! (m/fmap count (ListData. 5))))))
     (t/is (= 10 (muse/run!! (m/fmap count (ListData. 10)))))
     (t/is (= 15 (muse/run!! (m/bind (SingleData. 10) (fn [num] (SingleData. (+ 5 num)))))))))

#?(:cljs
   (t/deftest runner-macros
     ;; Stuck here because can't find run!
     #_(async done (go (run! (m/fmap count (ListData. 5)))))
     #_(async done
            (go
              (is (= 5 (ListData. 5)))))
     #_(t/is (= 10 (muse/run!! (m/fmap count (ListData. 10)))))
     #_(t/is (= 15 (muse/run!! (m/bind (SingleData. 10) (fn [num] (SingleData. (+ 5 num)))))))))

(comment

  
  
  
  

  

  (t/deftest cats-syntax
    (t/is (= 30 (run!! (m/mlet [x (List. 5)
                                y (List. 10)
                                z (Single. 15)]
                               (m/return (+ (count x) (count y) z))))))))
