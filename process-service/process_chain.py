import argparse
import time
import logging


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

LOG_LINE_FORMAT = '%(asctime)s %(name)s %(levelname)s >> %(message)s'
LOG_DATE_FORMAT = '%Y-%m-%d %H:%M:%S'

def simulate_process(plan_id):
    logger.info(f"Process started with planId={plan_id}")
    time.sleep(20)  # Simulate a long process (20 seconds)
    logger.info(f"Process finished with planId={plan_id}")
    return f"RETURN finished planId={plan_id}"

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="Process simulate")
    parser.add_argument("--plan", type=int, required=True, help="ID плана")
    args = parser.parse_args()
    handler = logging.FileHandler(f"log_plan_{args.plan}.txt", mode='a')
    formatter = logging.Formatter(LOG_LINE_FORMAT, LOG_DATE_FORMAT)
    handler.setFormatter(formatter)
    logger.addHandler(handler)


    res = simulate_process(args.plan)
    logger.info(f"Process UPDATE to --> FINISHED: {res}")