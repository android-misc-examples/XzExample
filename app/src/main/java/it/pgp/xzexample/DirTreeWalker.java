package it.pgp.xzexample;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by pgp on 02/11/16
 */
class DirTreeWalker implements Iterator<File>{
    private Stack<File> stack;
    DirTreeWalker(File rootDir) {
        if (!rootDir.isDirectory())
            throw new RuntimeException("Not a directory");
        stack = new Stack<>();
        for (File x : rootDir.listFiles()) stack.push(x);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    // only adds files, and directories in which subtree there is at least a file as leaf
//    @Override
//    public File next() {
//        // get next regular file from stack (if file is dir, expand it and go on)
//        File f = stack.pop();
//        while (f.isDirectory()) {
//            for (File x : f.listFiles()) stack.push(x);
//            f = stack.pop();
//        }
//
//        return f;
//    }

    @Override
    public File next() {
        // get next regular file from stack (if file is dir, expand it and go on)
        File f = stack.pop();
        if (f.isDirectory()) {
            for (File x : f.listFiles()) stack.push(x);
        }

        return f;
    }
}
