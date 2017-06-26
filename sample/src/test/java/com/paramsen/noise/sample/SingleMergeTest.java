package com.paramsen.noise.sample;

import org.junit.Test;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * @author Pär Amsen 06/2017
 */
public class SingleMergeTest {
    @Test
    public void doRequestsInParallelTest() {
        Single.merge(req1(), req2(), req3())
                .collect(ArrayList<String>::new, List<String>::add)
                .subscribe(success -> {
                    for (String result : success)
                        System.out.println(result);
                }, e -> {
                    //one of 'em failed
                    e.printStackTrace();
                });
    }

    public Single<String> req1() {
        return Single.create(sub -> {
            //do request
            sub.onSuccess("Request result");
            //on error
            //sub.onError(new IOException("Request error"));
        });
    }

    public Single<String> req2() {
        return Single.create(sub -> {
            //do request
            sub.onSuccess("Request result");
            //on error
            //sub.onError(new IOException("Request error"));
        });
    }

    public Single<String> req3() {
        return Single.create(sub -> {
            //do request
            sub.onSuccess("Request result");
            //on error
            //sub.onError(new IOException("Request error"));
        });
    }
}
