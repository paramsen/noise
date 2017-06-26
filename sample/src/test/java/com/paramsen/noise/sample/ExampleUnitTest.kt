package com.paramsen.noise.sample

import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        Single.merge(req1(), req2(), req3())
                .collect({ ArrayList<String>() }, { list, res -> list.add(res) })
                .subscribe({ success ->

                }, { err -> })
    }

    fun req1(): Single<String> {
        return Single.create({ emitter ->
            //do request
            emitter.onSuccess("Success")
            //if error
            //emitter.onError(IOException("Network exception"))
        })
    }

    fun req2(): Single<String> {
        return Single.create({ emitter ->
            //do request
            emitter.onSuccess("Success")
            //if error
            //emitter.onError(IOException("Network exception"))
        })
    }

    fun req3(): Single<String> {
        return Single.create({ emitter ->
            //do request
            emitter.onSuccess("Success")
            //if error
            //emitter.onError(IOException("Network exception"))
        })
    }
}
