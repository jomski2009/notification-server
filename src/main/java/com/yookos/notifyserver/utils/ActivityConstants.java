package com.yookos.notifyserver.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActivityConstants {

    public static enum Family {
        hasContent,
        sendToRecommender,
        isExclusion,
        isFeedback,
        mostlyOrdered
    }

    public static enum Type {

        /**
         * Viewed an item.
         */
        viewed(0,Family.hasContent,Family.sendToRecommender),
        /**
         * Created an item.
         */
        created(1,Family.hasContent,Family.sendToRecommender),
        /**
         * Modified an item.
         */
        modified(2,Family.hasContent,Family.sendToRecommender),
        /**
         * Commented on an item.
         */
        @Deprecated
        commented(3),
        /**
         * Created a reply to an item.
         */
        @Deprecated
        replied(4),
        /**
         * Voted on an item.
         */
        voted(5,Family.sendToRecommender),
        /**
         * Completed an item.
         */
        completed(6,Family.sendToRecommender),
        /**
         * Updated personal status.
         */
        @Deprecated
        updatedStatus(7),
        /**
         * Bookmarked an item or URL.
         */
        bookmarked(8, Family.sendToRecommender),
        /**
         * Rated an item.
         */
        rated(9,Family.sendToRecommender),
        /**
         * Performed an activity that doesn't fit any of the other options. The activity verb will be
         * rendered as an empty string, which the activity payload should account for.
         */
        @Deprecated
        blank(10),
        /**
         * Liked an item.
         */
        liked(11,Family.sendToRecommender,Family.mostlyOrdered),
        /**
         * Joined a container (social group)
         */
        joined(12,Family.isExclusion,Family.sendToRecommender),
        /**
         * Connection made between users (follow, friending)
         */
        connected(13,Family.sendToRecommender),
        /**
         * An object in the system has been followed
         */
        followed(14,Family.isExclusion,Family.sendToRecommender,Family.mostlyOrdered),
        /**
         * An object in the system has been unfollowed
         */
        unfollowed(15,Family.sendToRecommender,Family.mostlyOrdered),
        /**
         * An activity object has been read
         */
        read(16,Family.hasContent,Family.sendToRecommender,Family.mostlyOrdered),

        shared(17,Family.sendToRecommender),

        NOTFOUND(18,Family.sendToRecommender),

        UNAUTHORIZED(19,Family.sendToRecommender),

        mentioned(20,Family.sendToRecommender),

        /**
         * When a user achieves a new status level
         */
        promoted(21,Family.sendToRecommender),

        clicked(22,Family.sendToRecommender),

        logged_in(23,Family.sendToRecommender),

        logged_out(24,Family.sendToRecommender),

        /**
         * Generic activity event for applying something to an object. This
         * could be used to indicate a tagset or tag being applied to object.
         */
        applied(25,Family.sendToRecommender),

        /**
         * Generic activity event for removing something from an object. This
         * could be used to indicate a tagset or tag being removed from an object.
         */
        removed(26,Family.sendToRecommender),

        repost(27,Family.sendToRecommender),

        /**
         * Activity on an entity in the system has been excluded from a user's stream
         */
        object_exclusion_added(28,Family.isExclusion,Family.sendToRecommender),
        /**
         * Any exclusions on activity for a given entity have been removed for a particular user's stream
         */
        object_exclusion_removed(29,Family.isExclusion,Family.sendToRecommender),
        /**
         * Activity within a context has been excluded from a user's stream
         */
        context_exclusion_added(30,Family.isExclusion,Family.sendToRecommender),
        /**
         * Any exclusions on activity within a given context have been removed for a particular user's stream
         */
        context_exclusion_removed(31,Family.isExclusion,Family.sendToRecommender),

        user_deleted(32,Family.sendToRecommender),

        unread(33,Family.sendToRecommender,Family.mostlyOrdered),

        /*No longer used*/
        @Deprecated
        register_database(34),

        manage(35,Family.sendToRecommender,Family.mostlyOrdered),

        unmanage(36,Family.sendToRecommender,Family.mostlyOrdered),

        tracked(37,Family.sendToRecommender,Family.mostlyOrdered),

        untracked(38,Family.sendToRecommender,Family.mostlyOrdered),

        allread(39,Family.sendToRecommender,Family.mostlyOrdered),

        inUserStream(40,Family.isExclusion,Family.sendToRecommender),

        inUserInBox(41,Family.isExclusion,Family.sendToRecommender),

        inUserActivityQueue(42,Family.isExclusion,Family.sendToRecommender),

        unliked(43,Family.sendToRecommender,Family.mostlyOrdered),

        projectCompleted(44,Family.sendToRecommender),

        disinterest(45,Family.sendToRecommender,Family.isFeedback),

        notification(46,Family.sendToRecommender),

        watch(47,Family.sendToRecommender,Family.mostlyOrdered),

        unwatch(48,Family.sendToRecommender,Family.mostlyOrdered),

        dismiss(49,Family.sendToRecommender),

        unconnected(50,Family.sendToRecommender),

        reshred_complete(51,Family.sendToRecommender),

        /**
         * No longer a member of a container (Social group)
         */
        unjoined(52,Family.sendToRecommender),

        /**
         * EAE admin console connectivity tracer
         */
        trace(53),

        /**
         * Used to keep recommender aware of running cores
         */
        heartbeat(54,Family.sendToRecommender),

        moved(55, Family.sendToRecommender),

        /**
         * When a recommendation is returned for something a user is already following
         * we send this activity type to attempt to repair the invalid follow relationship.
         */
        repairFollowHint(56, Family.sendToRecommender),

        search(57, Family.sendToRecommender),

        user_search(58, Family.sendToRecommender),

        /**
         * Activity on an entity in the system has been untracked in a user's communications stream
         */
        object_untrack_added(59,Family.isExclusion,Family.sendToRecommender),
        /**
         * Any untracks on activity for a given entity have been removed for a particular user's communications stream
         */
        object_untrack_removed(60,Family.isExclusion,Family.sendToRecommender),

        /**
         *
         */
        digest(61, Family.sendToRecommender),

        correct_answer_set(62, Family.sendToRecommender, Family.mostlyOrdered),

        correct_answer_removed(63, Family.sendToRecommender, Family.mostlyOrdered),

        /**
         * Tagged an item.
         */
        tagged(64, Family.sendToRecommender, Family.mostlyOrdered);

        private static Map<Integer, Type> typeMap = new ConcurrentHashMap<Integer, Type>();
        static {
            for (Type type : values()) {
                typeMap.put(type.getID(), type);
            }
        }

        private int id;
        private Set<Family> families = new HashSet<Family>();

        Type(int id,Family... families) {
            this.id = id;
            this.families.addAll(Arrays.asList(families));

        }

        public static Type valueOf(int id) {
            return typeMap.get(id);
        }

        public boolean isMemberOf(Family family) {
            return families.contains(family);
        }

        public int getID() {
            return id;
        }

        public String toString() {
            // Special-case the activity type "blank".
            if (id == 10) {
                return "";
            } else return super.toString();
        }
    }

}