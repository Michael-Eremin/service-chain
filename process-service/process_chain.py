import argparse
import time

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def simulate_process(plan_id):
    logger.info(f"Process started with planId={plan_id}")
    time.sleep(20)  # Simulate a long process (20 seconds)
    logger.info(f"Process finished with planId={plan_id}")
    return f"RETURN Process finished with planId={plan_id}"

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Process simulate")
    parser.add_argument("--plan", type=int, required=True, help="ID плана")
    args = parser.parse_args()

    simulate_process(args.plan)