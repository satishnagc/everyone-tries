package com.satishnagc.myretail.exception


class MyRetailRetryEnabledException extends RuntimeException{
    private static final long serialVersionUID = 1L

    MyRetailRetryEnabledException() {}

    MyRetailRetryEnabledException(String message) {
        super(message)
    }

    MyRetailRetryEnabledException(String message, Throwable cause) {
        super(message,cause)
    }


//    @Override
//    Throwable fillInStackTrace() {
//        //Do Nothing
//    }

}
