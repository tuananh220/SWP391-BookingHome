package entity;

public class HostCancellationSummary {

    private int acceptedBookings;
    private int hostCancelledBookings;

    public int getAcceptedBookings() {
        return acceptedBookings;
    }

    public void setAcceptedBookings(int acceptedBookings) {
        this.acceptedBookings = acceptedBookings;
    }

    public int getHostCancelledBookings() {
        return hostCancelledBookings;
    }

    public void setHostCancelledBookings(int hostCancelledBookings) {
        this.hostCancelledBookings = hostCancelledBookings;
    }

    public int getCancellationRate() {
        if (acceptedBookings == 0) {
            return 0;
        }
        return (int) Math.round(
                hostCancelledBookings * 100.0 / acceptedBookings
        );
    }
}
