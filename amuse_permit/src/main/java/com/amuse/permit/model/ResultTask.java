package com.amuse.permit.model;

@SuppressWarnings("unused")
public class ResultTask<TResult> {
    public static class Result<TResult> {
        boolean isSuccess;
        boolean hasException;
        Exception exception;
        TResult resultData;
        Class<?> resultClass;

        public boolean isSuccess() {
            return isSuccess;
        }

        public boolean hasException() {
            return hasException;
        }

        public TResult getResultData() {
            return resultData;
        }

        public Exception getException() {
            return exception;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public void setHasException(boolean hasException) {
            this.hasException = hasException;
        }

        @SuppressWarnings("unchecked")
        public void setResultData(Object resultData) {
            this.resultData = (TResult) resultData;
        }
    }

    public interface onTaskCompleteListener {
        void onComplete(Result<?> result);
    }

    public ResultTask.onTaskCompleteListener mOnInvokeAttached;
    protected ResultTask.onTaskCompleteListener mOnTaskCompleteListener;

    public ResultTask<TResult> setOnTaskCompleteListener(onTaskCompleteListener mOnTaskCompleteListener) {
        this.mOnTaskCompleteListener = mOnTaskCompleteListener;
        return this;
    }

    public void invokeTask() {
        mOnInvokeAttached.onComplete(null);
    }

    public void callCompleteListener(Result<?> result) {
        if(mOnTaskCompleteListener != null) {
            mOnTaskCompleteListener.onComplete(result);
        }
    }
}
