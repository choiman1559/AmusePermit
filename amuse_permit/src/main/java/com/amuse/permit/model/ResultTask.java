package com.amuse.permit.model;

/**
 * Asynchronous operation and result retrieval class
 */
@SuppressWarnings("unused")
public class ResultTask<TResult> {
    /**
     * Result encapsulation class
     */
    public static class Result<TResult> {
        boolean isSuccess;
        boolean hasException;
        Exception exception;
        TResult resultData;
        Class<?> resultClass;

        /**
         * Check if the task is successfully completed
         *
         * @return true if the task is successfully completed
         */
        public boolean isSuccess() {
            return isSuccess;
        }

        /**
         * Check if the task has exceptions
         * It is possible to true even if the {@link #isSuccess()} is true
         *
         * @return true if the task has exception
         */
        public boolean hasException() {
            return hasException;
        }

        /**
         * Get the result object of the task.
         * Returns null if the task has no result object (for example, the task returned {@link Void} for result)
         *
         * @return Returns task result object
         */
        public TResult getResultData() {
            return resultData;
        }

        /**
         * Get the result exception of the task.
         * Returns null if the task has no exception.
         *
         * @see #hasException()
         * @return Returns task result exception
         */
        public Exception getException() {
            return exception;
        }

        /**
         * Set if the task successfully completed
         *
         * @param success if task is successful completed
         */
        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        /**
         * Set task result exception
         *
         * @param exception the exception object to be set
         */
        public void setException(Exception exception) {
            this.exception = exception;
        }

        /**
         * Set if the task result has an exception
         *
         * @param hasException true if the task result has an exception
         */
        public void setHasException(boolean hasException) {
            this.hasException = hasException;
        }

        /**
         * Set the task result object
         *
         * @param resultData the task result object
         */
        @SuppressWarnings("unchecked")
        public void setResultData(Object resultData) {
            this.resultData = (TResult) resultData;
        }
    }

    /**
     * Listener interface to wait for the result of an asynchronous operation.
     *
     * @see #setOnTaskCompleteListener(onTaskCompleteListener)
     */
    public interface onTaskCompleteListener {
        void onComplete(Result<?> result);
    }

    /**
     * A listener that handles the start of a task,
     * used in the internal implementation of AmusePermit.
     *
     * @see #invokeTask()
     */
    public ResultTask.onTaskCompleteListener mOnInvokeAttached;
    protected ResultTask.onTaskCompleteListener mOnTaskCompleteListener;

    /**
     * Set up a listener to receive completion data when the task is completed.
     *
     * @return Returns This instance to call {@link #invokeTask()} function
     * @param mOnTaskCompleteListener Listener object to receive the task results
     */
    public ResultTask<TResult> setOnTaskCompleteListener(onTaskCompleteListener mOnTaskCompleteListener) {
        this.mOnTaskCompleteListener = mOnTaskCompleteListener;
        return this;
    }

    /**
     * A function that starts an asynchronous task,
     * and the registered listener is called when the task is complete.
     *
     * @see #setOnTaskCompleteListener(onTaskCompleteListener)
     */
    public void invokeTask() {
        mOnInvokeAttached.onComplete(null);
    }

    /**
     * Function that calls the registered listener when the task is finished,
     * used in the internal implementation of AmusePermit.
     *
     * @param result The result object to be passed to the listener.
     */
    public void callCompleteListener(Result<?> result) {
        if(mOnTaskCompleteListener != null) {
            mOnTaskCompleteListener.onComplete(result);
        }
    }
}
