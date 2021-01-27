package gr.uom.socialmediaaggregator.api.comparators;

import gr.uom.socialmediaaggregator.data.model.Post;

public class NewestToOldestPostComparator implements IPostComparator {

    @Override
    public int compare(Post o1, Post o2) {
        int result = o1.getPostedDate().compareTo(o2.getPostedDate());
        return Integer.compare(0, result);
    }

}
